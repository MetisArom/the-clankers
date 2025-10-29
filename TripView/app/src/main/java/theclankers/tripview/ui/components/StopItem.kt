package theclankers.tripview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theclankers.tripview.classes.Stop

// For reference, look at the Trip 1 Screen, these are each of the stops


// Takes as input the stop object (https://lucid.app/lucidchart/4b3e1b22-e5ef-49e9-b20b-d5a6e5e591e9/edit?page=0_0&invitationId=inv_bb1cdee1-aef2-44e0-806a-2a3ccf4c65bf#)

// Uses the checkbox component to indicate whether or not this stop was marked completed
// Displays icon, stop name, and duration in hours


// Clicking this stop opens up the Stop in its own screen
@Composable
fun StopItem(
    stop: Stop,
    onStopClick: (Stop) -> Unit,
    onCompletedChange: (Stop, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showDragHandle: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onStopClick(stop) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (stop.completed)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stop.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (showDragHandle) {
                Icon(
                    imageVector = Icons.Rounded.Search, // replace with a proper drag icon
                    contentDescription = "Drag handle",
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else {
                Checkbox(
                    checked = stop.completed,
                    onCheckedChange = { checked -> onCompletedChange(stop, checked) }
                )
            }
        }
    }
}
