package theclankers.tripview.ui.components

// Takes as input a single trip object, containing the trip name and description

// Trip Name
// Trip description


// Clicking this component ALWAYS opens up the trip screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import theclankers.tripview.ui.theme.Purple4


/**
 * Displays a single trip item with a name and description.
 * Clicking this component always opens the trip screen.
 *
 * @param tripName The title of the trip.
 * @param tripDescription A short description or detail about the trip.
 * @param onClick Callback when the trip card is clicked.
 */
@Composable
fun TripItem(
    //for homepage
    tripName: String,
    tripDescription: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Purple4),
        modifier = Modifier
            .fillMaxWidth()
            .padding( vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            HelperText2(
                text = tripName,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            HelperText(
                text = tripDescription
            )
        }
    }
}
