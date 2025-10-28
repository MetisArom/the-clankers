package theclankers.tripview.ui.components

// Simple abstract image component,
// Used for consistency, probably just a wrapper for a simpler image component
// Takes as input just source
// exposes onClick functionality

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.painter.Painter
import theclankers.tripview.ui.theme.Purple40

@Composable
fun SimpleImage(
    source: Int, // Can be a URL (String) or drawable resource (Int)
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: (() -> Unit)? = null
) {
    val painter: Painter = when (source) {
        is Int -> painterResource(id = source) // Local drawable
        //is String -> rememberAsyncImagePainter(source) // Remote URL
        else -> throw IllegalArgumentException("Unsupported image source type")
    }

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .clip(CircleShape)
            .border(2.dp, Purple40, CircleShape)
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
    )
}
