package theclankers.tripview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onStopClick(stop) } // opens stop detail screen
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = stop.completed,
            onCheckedChange = { checked -> onCompletedChange(stop, checked) }
        )

        Spacer(Modifier.width(16.dp))

        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Stop icon",
            tint = Color.Gray
        )

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = stop.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}