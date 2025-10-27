package theclankers.tripview.ui.components


// Use the Trips screen as reference, just make it look the same.
// Same functionality as a button, exposes clickability, ya know


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import theclankers.tripview.ui.theme.Purple4

/**
 * Create Trip Button component.
 * Displays a dashed border card with title and description.
 * Same functionality as a button with exposed clickability.
 *
 * @param onClick Callback when the button is clicked
 * @param modifier Optional modifier for additional styling
 */
@Composable
fun CreateTripButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Purple4),
        //border = BorderStroke(
            //width = 1.dp,
            //color = Color(0xFFBDBDBD)
        //)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            HeaderText(
                text = "Create New Trip",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            HelperText(
                text = "Fill in form to supply context to LLM.",
            )
        }
    }
}