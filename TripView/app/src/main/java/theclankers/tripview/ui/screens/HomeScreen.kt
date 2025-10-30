package theclankers.tripview.ui.screens

import TripViewViewModel
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import theclankers.tripview.ui.navigation.navigateTo

@Composable
fun DebugScreen(navController: NavHostController) {
    //LocalActivity.current as ComponentActivity is for basically global state shared across screens.
    //You can have additional view models for each screen with navBackStackEntry.viewModel()!
    val activityVM: TripViewViewModel = viewModel(LocalActivity.current as ComponentActivity)
    Column {
        Button(onClick = {
            navigateTo(navController, "navigation")
        })
        { Text("Go to Navigation Screen")  }
        Button(onClick = {
            activityVM.toggleNavbar()
        })
        { Text("Toggle navbar (current val: ${activityVM.showNavbar})")  }

        //this is just temporary until we make authentication
        //after you authenticate, that will directly take you to trips.kt
        Button(onClick = {
            navigateTo(navController, "trips")
        })
        { Text("Go to TripCreation")  }

        Button(onClick = {
            navigateTo(navController, route = "sampleTrip")
        })
        { Text(text="Sample Itinerary Debug") }
        Button(onClick = {
            navigateTo(navController, route = "EditItinerary")
        })
        { Text(text="Edit Itinerary Debug") }


    }
}