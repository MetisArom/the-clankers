package theclankers.tripview.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import theclankers.tripview.ui.navigation.BottomNavItem

@Composable
fun Header(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route?.substringBefore("?")

    val rootRoutes = listOf(
        BottomNavItem.Trips.route,
        BottomNavItem.Camera.route,
        BottomNavItem.Friends.route,
        BottomNavItem.Profile.route,
        BottomNavItem.Debug.route
    )

    // Only show the arrow if you're NOT on a root route
    val canGoBack = navController.previousBackStackEntry != null && currentRoute !in rootRoutes

    TopAppBar(
        title = { Text(currentRoute ?: "TripView") },
        navigationIcon = {
            if (canGoBack) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF7F6F8)
        )
    )
}
