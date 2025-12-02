package theclankers.tripview.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor

@Composable
fun StarRating(
    rating: Double,               // decimal 1.0–5.0
    modifier: Modifier = Modifier,
    starSize: Dp = 24.dp,
    starColor: Color = Color(0xFFFFD700) // gold
) {
    val fullStars = floor(rating).toInt()
    val hasHalfStar = (rating - fullStars) >= 0.5
    val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

    Row(modifier = modifier) {

        // Full stars
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }

        // Half star (if needed)
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }

        // Empty stars
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Default.StarOutline,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }
    }
}