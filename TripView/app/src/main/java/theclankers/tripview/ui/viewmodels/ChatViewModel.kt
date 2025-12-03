import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import theclankers.tripview.data.models.Chatt
import java.time.Instant
import java.util.*
import theclankers.tripview.data.api.ApiClient
import java.util.UUID.randomUUID
import androidx.lifecycle.ViewModelProvider

class ChatViewModelFactory(
    private val tripId: Int,
    private val token: String,
    private val model: String = "models/gemini-2.5-flash"
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(tripId, token, model) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ChatViewModel(
    val tripId: Int,
    val token: String,
    val model: String = "models/gemini-2.5-flash"
) : ViewModel() {

    // Holds all chat messages being shown (mutableStateListOf to match your composable)
    var chatts = mutableStateListOf<Chatt>()
        private set

    val message = TextFieldState("")
    val instruction = "Type a message..."
    val errMsg = mutableStateOf("")
    val username = model

    /** Sends a chat message and streams the Gemini LLM response via SSE. */
    fun llmChat(chatt: Chatt, errMsgState: MutableState<String>) {
        // Add user message to chatts immediately so it shows in UI
        chatts.add(chatt)

        viewModelScope.launch {
            // Temporary assistant message/chatt as chunks come in
            var assistantChatt: Chatt? = null

            ApiClient.llmChatSse(
                tripId = tripId,
                message = chatt.message?.value ?: "", // user's message
                model = model,
                token = token,
                onEvent = { role, chunk ->
                    // Stream assistant chunk
                    if (role == "model") {
                        // First chunk: create a new assistant message
                        if (assistantChatt == null) {
                            assistantChatt = Chatt(
                                username = "model",
                                message = mutableStateOf(chunk),
                                id = randomUUID(),
                                timestamp = Instant.now().toString(),
                                "model"
                            )
                            chatts.add(assistantChatt!!)
                        } else {
                            // Add chunk to last message (assistant)
                            assistantChatt?.message?.value += chunk
                        }
                    }
                },
                onError = { error ->
                    errMsgState.value = error
                }
            )
        }
    }
}
