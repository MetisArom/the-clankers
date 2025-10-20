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

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    // don't delete the icon or i will be sad
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Camera : BottomNavItem("camera", "Camera", Icons.Default.Search)
    object Friends : BottomNavItem("friends", "Friends", Icons.Default.Search)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun TripViewNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        composable(BottomNavItem.Camera.route) { CameraScreen(navController) }
        composable(BottomNavItem.Friends.route) { FriendsScreen() }
        composable(BottomNavItem.Profile.route) { ProfileScreen() }
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