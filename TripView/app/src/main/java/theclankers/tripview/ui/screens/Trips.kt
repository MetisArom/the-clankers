package theclankers.tripview.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.CreateTripButton
import theclankers.tripview.ui.components.HeaderText
import theclankers.tripview.ui.components.HelperText
import theclankers.tripview.ui.components.TripItem

@Composable
fun TripsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F8))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // === Your Trips Section ===
        HeaderText(
            text = "Your Trips",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Create New Trip Card
        CreateTripButton(
            onClick = { navController.navigate("tripcreationform") },
            modifier = Modifier.padding(bottom = 24.dp)
        )



        // Example Trips
        TripItem(
            tripName = "Trip 1",
            tripDescription = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
            onClick = {
                navController.navigate("tripdetail/1")
                //will update this as i make more screens }
            })
    }}
