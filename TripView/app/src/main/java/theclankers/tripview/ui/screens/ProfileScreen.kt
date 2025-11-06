package theclankers.tripview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.ListComponent
import theclankers.tripview.ui.components.ProfileComponent
import theclankers.tripview.ui.components.TripItem
import theclankers.tripview.ui.viewmodels.useActiveTrips
import theclankers.tripview.ui.viewmodels.useAppContext

@Composable
fun ProfileScreen(navController: NavController) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value
    val userId = appVM.userIdState.value

    if (token == null || userId == null) return

    val activeTripsState = useActiveTrips(token, userId)
    val activeTrips: List<Int>? = activeTripsState.value

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF7F6F8))
        .padding(16.dp)
        .verticalScroll(rememberScrollState())){

        ProfileComponent(navController, userId)

        ListComponent(itemIds = activeTrips ?: emptyList()) { tripId ->
            TripItem(tripId = tripId, navController = navController)
        }
    }

}