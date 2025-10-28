package theclankers.tripview.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.serialization.json.Json
import theclankers.tripview.classes.Stop
import theclankers.tripview.ui.screens.DebugScreen
import theclankers.tripview.ui.screens.EditProfileScreen
import theclankers.tripview.ui.screens.FriendsListScreen
import theclankers.tripview.ui.screens.NavigationScreen
import theclankers.tripview.ui.screens.StopScreen
import theclankers.tripview.ui.screens.TripCreationForm
import theclankers.tripview.ui.screens.TripsScreen

@Composable
fun TripViewNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "trips") {
        //Create an entry here for each route following the format
        //composable([route]]) { [composable] }
        //if you need navigation other than the nav bar on that route,
        //you must pass in navController to use navigateTo on that page.
        //composable("home") { HomeScreen(navController) }
        composable("camera") { CameraScreen(navController) }
        //composable("friends") { FriendsScreen() }
        composable("friends") { FriendsListScreen() }
        composable("profile") { ProfileScreen(navController) }
        composable("editProfile") { EditProfileScreen(navController) }
        composable("camera2") { Camera2Screen() }
        composable("navigation") { NavigationScreen(navController) }
        composable("stops/{stop}", arguments = listOf(navArgument("stop") { type = NavType.StringType })) {
            backStackEntry ->
                val stopJson = backStackEntry.arguments?.getString("stop")
                if (stopJson != null) {
                    val stop = Json.decodeFromString<Stop>(stopJson)
                    StopScreen(navController, stop)
                } else {
                    goBack(navController)
                }
            }
        composable("trips") { TripsScreen(navController) }
        composable("tripcreationform") {
            TripCreationForm(navController = navController)
        }
        composable("debug") { DebugScreen(navController) }
    }
}

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route)
}

fun goBack(navController: NavController) {
    navController.popBackStack()
}