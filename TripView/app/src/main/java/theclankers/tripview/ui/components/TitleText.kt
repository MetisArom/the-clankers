package theclankers.tripview.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import theclankers.tripview.ui.theme.Black

@Composable
fun TitleText(
    //for YourTrips
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Black
) {
    Text(
        text = text,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = modifier
    )
}