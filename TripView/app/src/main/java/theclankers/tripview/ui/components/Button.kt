package theclankers.tripview.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


// Different types:
// Edit Profile button style
// Dark purple clickable button style



// Button size
// Icon
// Button background color
// Button text color
// Button text (optional, if left empty would only show the icon)
// onClick functionality
// Button type (look at the difference between the edit profile button and other buttons, they have different shapes and stuff)

@Composable
fun SampleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text("Click Me")
    }
}