package theclankers.tripview.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import theclankers.tripview.ui.components.ListComponent
import theclankers.tripview.ui.components.StopItem
import theclankers.tripview.ui.navigation.goBack
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useTrip

@Composable
fun TripScreen(navController: NavHostController, tripId: Int) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val tripVM = useTrip(token, tripId)
    val stopIds = tripVM.stopIdsState.value

    if (stopIds == null) return

    ListComponent(stopIds) { stopId ->
        StopItem(stopId = stopId, navController = navController)
    }
}