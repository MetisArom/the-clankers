package theclankers.tripview.ui.components

import ChatViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import theclankers.tripview.R
import theclankers.tripview.data.models.Chatt
import java.time.Instant

@Composable
fun SubmitButton(vm: ChatViewModel, listScroll: LazyListState) {
    var isSending by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = {
            isSending = true
            vm.viewModelScope.launch(Dispatchers.Default) {
                vm.llmChat(
                    Chatt(
                        username = vm.model,
                        message = mutableStateOf(vm.message.text.toString()),
                        timestamp = Instant.now().toString(),
                        role = "user"
                    ), vm.errMsg)
                // completion code
                vm.message.clearText()
                isSending = false
                withContext(AndroidUiDispatcher.Main) {
                    listScroll.animateScrollToItem(vm.chatts.size)
                }
            }
        },
        // modifiers
        modifier = Modifier
            .size(55.dp)
            .background(if (vm.message.text.isEmpty()) Color.DarkGray else Color.Gray,
                shape = CircleShape),
        enabled = !(isSending || vm.message.text.isEmpty()),
    ) {
        // icons
        if (isSending) {
            CircularProgressIndicator(
                color = Color.Gray,
                strokeWidth = 4.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.send),
                tint = if (vm.message.text.isEmpty()) Color.Red else Color.Yellow,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
