package theclankers.tripview.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// import android.graphics.Color
// import androidx.compose.foundation.layout.Box
// import androidx.compose.foundation.layout.Row
// import androidx.compose.foundation.layout.Spacer
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.height
// import androidx.compose.foundation.layout.size
// import androidx.compose.foundation.layout.width
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.ArrowForward
// import androidx.compose.material.icons.filled.Check
// import androidx.compose.material3.ButtonDefaults
// import androidx.compose.material3.Icon
// import androidx.compose.material3.LocalContentColor
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.unit.Dp
// import androidx.compose.ui.unit.dp
// import theclankers.tripview.ui.theme.DarkGray
// import theclankers.tripview.ui.theme.LightGray
// import theclankers.tripview.ui.theme.Purple1
// import theclankers.tripview.ui.theme.Purple2
// import theclankers.tripview.ui.theme.Purple80
// import theclankers.tripview.ui.theme.White


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

// @Composable
// fun TripButton(
//     modifier: Modifier = Modifier,
//     enabled: Boolean,
//     onClick: () -> Unit,
//     buttonHeight: Dp = 56.dp,
//     buttonFraction: Float = 0.8f // width as fraction of parent
// ) {
//     Box(
//         modifier = Modifier.fillMaxSize(),
//         contentAlignment = Alignment.Center // centers content both vertically & horizontally
//     ) {
//         Button(
//             onClick = onClick,
//             enabled = enabled,
//             colors = ButtonDefaults.buttonColors(
//                 containerColor = if (enabled) Purple1 else LightGray,
//                 contentColor = if (enabled) White else DarkGray,
//                 disabledContainerColor = LightGray,
//                 disabledContentColor = DarkGray
//             ),
//             modifier = modifier
//                 .fillMaxWidth(buttonFraction) // makes it a narrower pill
//                 .height(buttonHeight),
//             shape = RoundedCornerShape(percent = 50) // pill-shaped oval
//         ) {
//             Row(verticalAlignment = Alignment.CenterVertically) {
//                 Icon(
//                     imageVector = Icons.Default.Check,
//                     contentDescription = "Check",
//                     modifier = Modifier.size(24.dp),
//                     tint = LocalContentColor.current
//                 )
//                 Spacer(modifier = Modifier.width(8.dp))
//                 Text(text = "Create Trip")
//             }
//         }
//     }
// }
