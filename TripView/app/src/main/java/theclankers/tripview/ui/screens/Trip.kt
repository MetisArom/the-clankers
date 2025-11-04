package theclankers.tripview.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import theclankers.tripview.ui.components.ListComponent
import theclankers.tripview.ui.components.StopItem
import theclankers.tripview.ui.navigation.goBack
import theclankers.tripview.ui.navigation.navigateTo
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useTrip

@Composable
fun TripScreen(navController: NavHostController) {
    //returns 0 if no argument, bounce back
    val tripId = navController.currentBackStackEntry?.arguments?.getInt("tripId") ?: 0
    if (tripId == 0) {
        goBack(navController)
        return
    }

//    val activityVM: AppViewModel = viewModel(LocalActivity.current as ComponentActivity)

    val tripViewModel = useTrip("token", tripId)
    val trip = tripViewModel.tripState.value

    ListComponent(trip?.stopIds ?: emptyList()) { stopId ->
        StopItem(stopId = stopId, navController = navController)
    }
}