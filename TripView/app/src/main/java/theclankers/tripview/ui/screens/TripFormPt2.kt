package theclankers.tripview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import theclankers.tripview.ui.components.*
import theclankers.tripview.ui.theme.PurpleGrey80
import theclankers.tripview.ui.viewmodels.TripViewModel

@Composable
fun TripFormPt2(
    navController: NavController,
    tripViewModel: TripViewModel = viewModel()
) {
    val trips by tripViewModel.trips.collectAsState()
    val stops by tripViewModel.stops.collectAsState()
    val isLoading by tripViewModel.isLoading.collectAsState()
    val errorMessage by tripViewModel.errorMessage.collectAsState()
    var selectedTripId by remember { mutableStateOf<Int?>(null) }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $errorMessage")
            }
            return
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            HeaderText(text = "Pick Itinerary")
            HelperText(text = "Please select one of the following itineraries:")
            Spacer(Modifier.height(8.dp))
        }

        items(trips) { trip ->
            val tripStops = stops.filter { it.tripId == trip.tripId }

            TripItem2(
                tripName = "Trip ${trip.tripId}: ${trip.status}",
                tripDescription = "Stops: ${tripStops.joinToString(" • ") { it.name }}",
                expandedContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = PurpleGrey80,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        tripStops.forEach { stop ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = stop.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    if (!stop.description.isNullOrBlank()) {
                                        Text(
                                            text = stop.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        TripButton(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            onClick = {
                                selectedTripId = trip.tripId
                                // You can navigate or perform your action here
                                // e.g., navController.navigate("tripDetails/${trip.tripId}")
                            }
                        )
                    }
                }
            )
        }

        item {
            Spacer(Modifier.height(24.dp))
        }
    }
}
