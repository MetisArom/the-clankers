package theclankers.tripview.ui.navigation

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

sealed class NavItem(val route: String, val label: String, val icon: ImageVector? = null) {
    // don't delete the icon for the top 4 or i will be sad
    object Home : NavItem("home", "Home", Icons.Default.Home)
    object Camera : NavItem("camera", "Camera", Icons.Default.Search)
    object Friends : NavItem("friends", "Friends", Icons.Default.Search)
    object Profile : NavItem("profile", "Profile", Icons.Default.Person)
    object Camera2 : NavItem("camera", "Camera 2", Icons.Default.Search)
}

@Composable
fun TripViewNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = NavItem.Home.route) {
        composable(NavItem.Home.route) { HomeScreen() }
        composable(NavItem.Camera.route) { CameraScreen() }
        composable(NavItem.Camera2.route) { Camera2Screen() }
        composable(NavItem.Friends.route) { FriendsScreen() }
        composable(NavItem.Profile.route) { ProfileScreen() }
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