package theclankers.tripview.ui.components

// For styling consistency, takes as input text, looks like the helper text that shows up on the Navigation screens and also in other places
// textColor exposed

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import theclankers.tripview.ui.theme.Black
import theclankers.tripview.ui.theme.PurpleGrey40
import theclankers.tripview.ui.theme.PurpleGrey80

/**
 * Supporting text component for styling consistency.
 * Used for helper text on Navigation screens and other places.
 *
 * @param text The text to display
 * @param textColor The color of the text (default: gray)
 * @param modifier Optional modifier for additional styling
 */
@Composable
fun HelperText(
    text: String,
    textColor: Color = PurpleGrey40,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 18.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            color = textColor
        ),
        modifier = modifier
    )
}
@Composable
fun HelperText2(
    //for the title of each trip (ie. Trip 1, Trip 2 on Trips screen on Figma)
    //also for friend name
    text: String,
    textColor: Color = Black,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            color = textColor
        ),
        modifier = modifier
    )
}