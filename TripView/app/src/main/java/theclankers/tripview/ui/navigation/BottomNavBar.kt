package theclankers.tripview.ui.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    // don't delete the icon or i will be sad
    object Trips : BottomNavItem("trips", "Trips", Icons.Default.Home)
    object Camera : BottomNavItem("camera", "Camera", Icons.Default.CameraAlt)
    object Friends : BottomNavItem("friends", "Friends", Icons.Default.Search)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
    //object Debug : BottomNavItem("debug", "Debug", Icons.Default.Build)
    object Login : BottomNavItem("login", "Login", Icons.Default.Person)
}

@Composable
fun TripViewNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Trips,
        BottomNavItem.Camera,
        BottomNavItem.Friends,
        BottomNavItem.Profile,
        //BottomNavItem.Debug
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier, tint = Color.Black) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navigateToRoot(navController, item.route)
                    }
                }
            )
        }
    }
}

@Composable
fun TripViewDemoNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Login,
        BottomNavItem.Camera
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier, tint = Color.Black) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    Log.d(null,item.route)
                    if (currentRoute != item.route) {
                        navigateToRoot(navController, item.route)
                    }
                }
            )
        }
    }
}