package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import theclankers.tripview.ui.components.ListComponent
import theclankers.tripview.ui.components.StopItem
import theclankers.tripview.ui.navigation.goBack
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useTrip

@Composable
fun TripScreen(navController: NavHostController, tripId: Int) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val tripVM = useTrip(token, tripId)
    val stops = tripVM.stops.value

    Column {
        Row {
            Button(onClick = {
                navigateToDetail(navController, "navigation/$tripId" )
            }) { Text("Navigation") }
            Button(onClick = { println("Chat clicked") }) { Text("Chat") }
            Button(onClick = { println("Edit clicked") }) { Text("Edit") }
        }

        for (stop in stops) {
            StopItem(navController, stop)
        }
    }

}