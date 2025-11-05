package theclankers.tripview.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import theclankers.tripview.ui.screens.*

/**
 * Main Navigation Graph for the TripView app
 * All screens/routes should be declared here.
 * Any screen that needs a resource (Trip, Stop, User) will just receive its ID.
 */
@Composable
fun TripViewNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "trips" // Default screen
    ) {

        // Camera screen (no arguments)
        composable("camera") { CameraScreen(navController) }

        // Friends list screen
        composable("friends") { FriendsListScreen(navController) }

        // Profile screen
        composable("profile") { ProfileScreen(navController) }

        // Edit profile screen
        composable("editProfile") { EditProfileScreen(navController) }

        // Navigation screen for a specific trip
        composable(
            "navigation/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) NavigationScreen(navController, tripId) else goBack(navController)
        }

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
