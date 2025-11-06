package theclankers.tripview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    onQuery: (String) -> Unit,
    prefill: String = "",
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf(prefill) }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search icon",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = query,
            onValueChange = {
                query = it
                onQuery(it)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done  // shows "Done" on keyboard
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus() // ✅ hides keyboard when Done pressed
                }
            ),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Search...",
                        color = Color.Gray,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    )
                }
                innerTextField()
            }
        )
    }
}
