package theclankers.tripview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.HeaderText

@Composable
fun TripsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F8))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderText(
            text = "Your Trips",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Create New Trip card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { navController.navigate("tripcreationform") },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Create New Trip", fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Fill in form to supply context to LLM.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        // Example trips
        TripCard(title = "Trip 1", description = "Supporting line text lorem ipsum dolor sit amet, consectetur.")
        TripCard(title = "Trip 2", description = "Supporting line text lorem ipsum dolor sit amet, consectetur.")

        Spacer(modifier = Modifier.height(16.dp))

        HeaderText(
            text = "Friends Trips",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TripCard(title = "Trip 5", description = "Supporting line text lorem ipsum dolor sit amet, consectetur.")

        Spacer(modifier = Modifier.height(16.dp))

        HeaderText(
            text = "Completed Trips",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TripCard(title = "Trip 6", description = "Supporting line text lorem ipsum dolor sit amet, consectetur.")
    }
}

@Composable
fun TripCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF5FF)) // light purple
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}