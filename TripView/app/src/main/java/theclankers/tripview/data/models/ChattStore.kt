package theclankers.tripview.data.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import theclankers.tripview.core.Constants.BASE_URL
import java.io.IOException
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@kotlinx.serialization.Serializable
data class OllamaError(val error: String)

//TODO: Update this to store response from our LLM
@Serializable
@JsonIgnoreUnknownKeys
data class OllamaReply(val model: String, val created_at: String, val response: String)

object ChattStore {
    var chatts = mutableStateListOf<Chatt>()
        private set

    private const val serverUrl = BASE_URL

    private val client = OkHttpClient.Builder()
        .connectTimeout(0, TimeUnit.MILLISECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    private suspend fun Call.await() = suspendCoroutine { cont ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                cont.resume(response)
            }
            override fun onFailure(call: Call, e: IOException) {
                cont.resumeWithException(e)
            }
        })
    }

    suspend fun llmPrompt(chatt: Chatt, errMsg: MutableState<String>) {

        chatts.add(chatt)

        // prepare prompt
        val jsonObj = mapOf(
            "model" to chatt.username,
            "prompt" to chatt.message?.value,
            "stream" to true,
        )
        val requestBody = JSONObject(jsonObj).toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        // prepare request
        //val apiUrl = "${serverUrl}/llmprompt"
        // my server for test, will need to make an endpoint and switch it
        val apiUrl = "https://3.21.34.104/llmprompt"
        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Accept", "application/*")
            .post(requestBody)
            .build()

        // connect to chatterd and Ollama
        try {
            val response = client.newCall(request).await()
            if (!response.isSuccessful) {
                errMsg.value = parseErr(response.code.toString(),
                    apiUrl, response.body.string())
                return
            }

            // prepare placeholder
            val resChatt = Chatt(
                username = "assistant (${chatt.username ?: "ollama"})",
                message = mutableStateOf(""),
                timestamp = Instant.now().toString()
            )
            chatts.add(resChatt)

            // receive Ollama response
            val stream = response.body.source()
            while (!stream.exhausted()) {
                val line = stream.readUtf8Line() ?: continue
                try {
                    val ollamaResponse = Json.decodeFromString<OllamaReply>(line)
                    resChatt.message?.value += ollamaResponse.response
                } catch (e: IllegalArgumentException) {
                    errMsg.value += parseErr(e.localizedMessage, apiUrl, line)
                    resChatt.message?.value += "\nllmPrompt Error: ${errMsg.value}\n\n"
                }
            }
        } catch (e: Throwable) {
            errMsg.value = "llmPrompt: ${e.localizedMessage ?: "failed"}"
        }
    }

    private fun parseErr(code: String?, apiUrl: String, line: String): String {
        try {
            val errJson = Json.decodeFromString<OllamaError>(line)
            return errJson.error
        } catch (e: IllegalArgumentException) {
            return "$code\n${apiUrl}\n${line}"
        }
    }
}