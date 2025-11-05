package theclankers.tripview.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import theclankers.tripview.ui.navigation.Header
import theclankers.tripview.ui.navigation.TripViewNavGraph
import theclankers.tripview.ui.navigation.TripViewNavigationBar
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.core.Constants.USERNAME
import theclankers.tripview.core.Constants.PASSWORD
import theclankers.tripview.ui.navigation.AuthNavGraph

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val appVM: AppViewModel = useAppContext()

    // Auto-login once
    LaunchedEffect(Unit) {
        appVM.login(USERNAME, PASSWORD)
    }

    val showNavbar = appVM.showNavbarState.value

    Scaffold(
        topBar = { Header(navController) },
        bottomBar = { if (showNavbar) TripViewNavigationBar(navController) },
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            if (appVM.isAuthedState.value) {
                Log.d("AppScaffold", "✅ User is authenticated, showing main app UI")
                TripViewNavGraph(navController)
            }
            else {
                Log.d("AppScaffold", "🔐 User is not authenticated, showing LoginScreen")
                AuthNavGraph(navController)
            }
        }
    }
}
