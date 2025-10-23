package theclankers.tripview.ui.navigation

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import theclankers.tripview.ui.screens.NavigationScreen

@Composable
fun TripViewNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        //Create an entry here for each route following the format
        //composable([route]]) { [composable] }
        //if you need navigation other than the nav bar on that route,
        //you must pass in navController to use navigateTo on that page.
        composable("home") { HomeScreen(navController) }
        composable("camera") { CameraScreen(navController) }
        composable("friends") { FriendsScreen() }
        composable("profile") { ProfileScreen() }
        composable("camera2") { Camera2Screen() }
        composable("navigation") { NavigationScreen(navController) }
    }
}

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}