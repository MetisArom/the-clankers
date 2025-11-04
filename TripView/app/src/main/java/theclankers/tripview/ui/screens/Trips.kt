package theclankers.tripview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.CreateTripButton
import theclankers.tripview.ui.components.TitleText
import theclankers.tripview.ui.components.ListComponent
import theclankers.tripview.ui.components.TripItem
import theclankers.tripview.ui.viewmodels.useActiveTrips
import theclankers.tripview.ui.viewmodels.useCompletedTrips
import theclankers.tripview.ui.viewmodels.useFriendsTrips

@Composable
fun TripsScreen(navController: NavController) {
    val activeTripsState = useActiveTrips("token", 1)
    val activeTrips: List<Int>? = activeTripsState.value

    val friendsTripsState = useFriendsTrips("token", 1)
    val friendsTrips: List<Int>? = friendsTripsState.value

    val completedTripsState = useCompletedTrips("token", 1)
    val completedTrips: List<Int>? = completedTripsState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F8))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TitleText(
            text = "Your Trips",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CreateTripButton(
            onClick = { navController.navigate("tripcreationform") },
            modifier = Modifier.padding(bottom = 24.dp)
        )
        ListComponent(itemIds = activeTrips ?: emptyList()) { tripId ->
            TripItem(tripId = tripId, navController = navController)
        }

        TitleText(
            text = "Friends Trips",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ListComponent(itemIds = friendsTrips ?: emptyList()) { tripId ->
            TripItem(tripId = tripId, navController = navController)
        }

        TitleText(
            text = "Completed Trips",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ListComponent(itemIds = completedTrips ?: emptyList()) { tripId ->
            TripItem(tripId = tripId, navController = navController)
        }
    }}
