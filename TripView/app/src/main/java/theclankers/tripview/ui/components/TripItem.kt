package theclankers.tripview.ui.components

// Takes as input a single trip object, containing the trip name and description

// Trip Name
// Trip description


// Clicking this component ALWAYS opens up the trip screen

import android.R.attr.description
import android.R.attr.name
import android.R.attr.onClick
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import theclankers.tripview.ui.theme.Purple4
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useTrip


@Composable
fun TripItem(navController: NavController, tripId: Int) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val tripVM = useTrip(token, tripId)
    val name = tripVM.nameState.value
    val description = tripVM.descriptionState.value

    if (name == null || description == null) return

    Log.d("TripItem", "Rendering TripItem for trip: $tripId")

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Purple4),
        modifier = Modifier
            .fillMaxWidth()
            .padding( vertical = 8.dp)
            .clickable { navController.navigate("trip/${tripId}") }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            HelperText2(
                text = name,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            HelperText(
                text = description
            )
        }
    }
}
