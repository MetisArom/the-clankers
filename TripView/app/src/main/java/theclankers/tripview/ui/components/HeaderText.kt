package theclankers.tripview.ui.components

// For consistent styling, takes as input text, for reference look at the Trip 1 screen on figma it's just the title text
// textColor exposed

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import theclankers.tripview.ui.theme.Black

@Composable
fun TripTitle(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Black
) {
    Text(
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = modifier
    )
}