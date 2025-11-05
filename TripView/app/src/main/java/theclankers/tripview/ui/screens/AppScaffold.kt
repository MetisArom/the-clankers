package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import theclankers.tripview.ui.navigation.Header
import theclankers.tripview.ui.navigation.TripViewNavGraph
import theclankers.tripview.ui.navigation.TripViewNavigationBar
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useAppContext

@Composable
fun AppScaffold(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    val appVM: AppViewModel = useAppContext()
    val showNavbar = appVM.showNavbarState.value

    Scaffold(
        topBar = { Header(navController) },
        bottomBar = { if (showNavbar) TripViewNavigationBar(navController) }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(innerPadding).padding(contentPadding)) {
            TripViewNavGraph(navController)
        }
    }
}
