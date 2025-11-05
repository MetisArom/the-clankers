package theclankers.tripview.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useAppContext

@Composable
fun DebugScreen(navController: NavHostController) {
    val appVM = useAppContext()

    Column {
        Button(onClick = {
            navigateToDetail(navController, "navigation/1")
        })
        { Text("Go to Navigation Screen")  }
        Button(onClick = {
            appVM.toggleNavbar()
        })
        { Text("Toggle navbar (current val: ${appVM.showNavbarState.value})")  }

        //this is just temporary until we make authentication
        //after you authenticate, that will directly take you to trips.kt
        Button(onClick = {
            navigateToDetail(navController, "trips")
        })
        { Text("Go to TripCreation")  }

//        Button(onClick = {
//            navigateTo(navController, route = "sampleTrip")
//        })
//        { Text(text="Hardcoded Itinerary Debug") }

        Button(onClick = {
            navigateToDetail(navController, route = "sampleTrip")
        })
        {Text(text="Itinerary Screen Debug")}

        Button(onClick = {
            navigateToDetail(navController, route = "EditItinerary")
        })
        { Text(text="Edit Itinerary Debug") }


    }
}