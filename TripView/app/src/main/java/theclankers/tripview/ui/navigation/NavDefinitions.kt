package theclankers.tripview.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.serialization.json.Json
import theclankers.tripview.data.models.Stop
import theclankers.tripview.ui.viewmodels.TripViewModel
import theclankers.tripview.ui.screens.*

/**
 * Main Navigation Graph for the TripView app
 * All screens/routes should be declared here.
 * Any screen that needs a resource (Trip, Stop, User) will just receive its ID.
 */
@Composable
fun TripViewNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "trips") {

        // Camera screen (no arguments)
        composable("camera") { CameraScreen(navController) }

        // Friends list screen
        composable("friends") { FriendsListScreen(navController) }

        // Profile screen
        composable("profile") { ProfileScreen(navController) }

        // Edit profile screen
        composable("editProfile") { EditProfileScreen(navController) }

        // BELOW HERE WAS NOT IN MAIN
        
        // Friend Profile Screen
        // composable("friendProfile") { FriendProfileScreen(navController) }
        
        // Navigation screen for a specific trip
        composable("navigation/{tripId}", arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) NavigationScreen(navController, tripId) else goBack(navController)
        }

        // Main camera screen
        composable("camera") { CameraScreen(navController)}
        
        // Camera confirmation screem
        composable(
            "cameraConfirmScreen/{photoPath}",
            arguments = listOf(navArgument("photoPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoPath = backStackEntry.arguments?.getString("photoPath")
            CameraConfirmScreen(photoPath,navController)
        }

        // Landmark Context Screen
        composable(
            "landmarkContext/{photoPath}",
            arguments = listOf(navArgument("photoPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoPath = backStackEntry.arguments?.getString("photoPath")
            LandmarkContextScreen(photoPath,navController)
        }

        // unknown navigation screen
        // composable("navigation") { NavigationScreen(navController) }

        // Screen for a stop of some kind
//        composable(
//            "stops/{stop}",
//            arguments = listOf(navArgument("stop") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val stopJson = backStackEntry.arguments?.getString("stop")
//            if (stopJson != null) {
//                val stop = Json.decodeFromString<Stop>(stopJson)
//                StopScreen(navController, stop)
//            } else {
//                goBack(navController)
//            }
//        }
        composable("trips") { TripsScreen(navController) }
        composable("tripcreationform") {
            TripCreationForm(navController = navController)
        }

        // ABOVE HERE WAS NOT IN MAIN

        // Trip screen for a specific trip
        composable(
            "trip/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) TripScreen(navController, tripId) else goBack(navController)
        }

        // Stop screen for a specific stop
        composable(
            "stop/{stopId}",
            arguments = listOf(navArgument("stopId") { type = NavType.IntType })
        ) { backStackEntry ->
            val stopId = backStackEntry.arguments?.getInt("stopId") ?: 0
            if (stopId != 0) StopScreen(navController, stopId) else goBack(navController)
        }

        // Trips list screen
        composable("trips") { TripsScreen(navController) }

        // Trip creation form
        composable("tripcreationform") { TripCreationForm(navController) }

        // Debug screen
        composable("debug") { DebugScreen(navController) }
        // Sample trip screen
        composable("sampleTrip") { SampleTrip(navController) }

        // Edit itinerary screen
        composable("editItinerary") { EditItinerary(navController) }
        
        // composable(
        //     route = "EditItinerary/{tripId}"
        // ) { backStackEntry ->
        //     val debugToken = "user_jwt_token"
        //     val tripId = backStackEntry.arguments?.getString("tripId")?.toInt()
        //     val tripViewModel = remember { TripViewModel(debugToken) }
        //     EditItinerary(navController, tripId, tripViewModel)
        // }
        //   composable("tripdetail/{tripId}", arguments = listOf(navArgument("tripId") { type = NavType.IntType })) { TripDetailsScreen(navController) }
        //   composable("ItineraryScreen"){ ItineraryScreen(navController, 1, viewModel)}
        // composable(
        //     route = "ItineraryScreen/{tripId}",
        //     arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        // ) { backStackEntry ->
        //     val tripId = backStackEntry.arguments?.getInt("tripId") ?: return@composable
        //     val tripViewModel: TripViewModel = viewModel()
   
        //     ItineraryScreen(
        //         navController = navController,
        //         tripId = tripId,
        //         viewModel = tripViewModel
        //     )
        // }
        // composable("ItineraryScreen/1") {
        //     val debugToken = "user_jwt_token"
   
        //     val tripViewModel = remember { TripViewModel(debugToken) }
        //     ItineraryScreen(navController, 1, tripViewModel)
        // }
        //   composable("stop/{stopId}") { backStackEntry ->
        //       val stop = backStackEntry.arguments?.getInt("stopId") ?: 0
        //       StopScreen(navController, stop)
        //   }
    }
}

/** Navigate to a specific detail route */
fun navigateToDetail(navController: NavController, route: String) {
    navController.navigate(route)
}

/** Navigate to a root route and clear back stack */
fun navigateToRoot(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Go back to the previous screen */
fun goBack(navController: NavController) {
    navController.popBackStack()
}
