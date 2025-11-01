package theclankers.tripview.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import theclankers.tripview.data.models.Trip
import theclankers.tripview.ui.navigation.goBack
import theclankers.tripview.ui.navigation.navigateTo
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useTrip
import theclankers.tripview.ui.viewmodels.useUser

@Composable
fun TripDetailsScreen(navController: NavHostController) {
    //returns 0 if no argument, bounce back
    val tripId = navController.currentBackStackEntry?.arguments?.getInt("tripId") ?: 0
    if (tripId == 0) {
        goBack(navController)
        return
    }

    val activityVM: AppViewModel = viewModel(LocalActivity.current as ComponentActivity)

    val userState = useTrip(activityVM.authAccessToken.value, tripId)
    val user = userState.value

    Column {
        Text("This is the trip page for trip ${user?.tripId}")
        Button(onClick = {
            goBack(navController)
        }) { Text("Go back") }
        Button(onClick = {
            navigateTo(navController, "navigation/$tripId")
        }) { Text("Navigation") }
    }

}