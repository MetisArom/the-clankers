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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        composable("stops/{itemId}", arguments = listOf(navArgument("itemId") { type = NavType.IntType })) {
            backStackEntry ->
                StopScreen(navController, backStackEntry.arguments?.getInt("itemId"))
            }
        }
}

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route)
}

fun goBack(navController: NavController) {
    navController.popBackStack()
}