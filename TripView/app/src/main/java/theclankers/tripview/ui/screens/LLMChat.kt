package theclankers.tripview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import theclankers.tripview.ChattViewModel
import theclankers.tripview.data.models.Chatt
import theclankers.tripview.data.models.ChattStore.chatts
import theclankers.tripview.ui.theme.PurpleGrey40
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import theclankers.tripview.R
import theclankers.tripview.ui.components.SubmitButton


//look at Figma, code same functionality

@Composable
fun ChattView(chatt: Chatt, isSender: Boolean) {
    Column(
        horizontalAlignment = if (isSender) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // chatt displayed here
        chatt.message?.let { msg ->
            if (msg.value.isNotEmpty()) {
                Text(
                    text = if (isSender) "" else chatt.username ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = PurpleGrey40,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )

                Text(
                    text = msg.value,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .shadow(2.dp, shape = RoundedCornerShape(20.dp))
                        .background(if (isSender) Color.Magenta else Color.White)
                        .padding(12.dp)
                        .widthIn(min = 50.dp, max = 350.dp)
                )

                Text(
                    text = chatt.timestamp ?: "",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ChattScrollView(modifier: Modifier, listScroll: LazyListState) {
    val vm: ChattViewModel = viewModel()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        state = listScroll,
    ) {
        items(items = chatts, key = { it.id as Any }) {
            ChattView(it, it.username == vm.username)
        }
    }
}

@Composable
fun ChatScreen() {
    val vm: ChattViewModel = viewModel()

    val layoutDirection = LocalLayoutDirection.current
    val listScroll = rememberLazyListState()
    val focus = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.chatter),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.White),
            )
        },
        // tap background to dismiss keyboard
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures { focus.clearFocus() }
            }
    ) {
            // describe the content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(
                        it.calculateStartPadding(layoutDirection),
                        it.calculateTopPadding(),
                        it.calculateEndPadding(layoutDirection),
                    )
                    .background(color = Color.White),
            ) {
                ChattScrollView(modifier = Modifier.weight(1f), listScroll)

                // prompt input and submit
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                ) {
                    OutlinedTextField(
                        state = vm.message,
                        placeholder = {
                            Text(text = vm.instruction, color = Color.Gray)
                        },
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        lineLimits = TextFieldLineLimits.MultiLine(1, 6),
                    )
                    SubmitButton(listScroll)
                }

                // show error
                if (vm.errMsg.value.isNotEmpty()) {
                    AlertDialog(
                        modifier = Modifier
                            .shadow(0.dp, shape = RoundedCornerShape(20.dp))
                            .padding(12.dp)
                            .widthIn(min = 30.dp, max = 300.dp),
                        onDismissRequest = {
                            vm.errMsg.value = ""
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                vm.errMsg.value = ""
                            }) {
                                Text(
                                    "OK",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        },
                        title = {
                            Text(
                                "LLM Error",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text(
                                vm.errMsg.value,
                                fontSize = 20.sp,
                                lineHeight = 24.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    )
                }
            }
        }
    }