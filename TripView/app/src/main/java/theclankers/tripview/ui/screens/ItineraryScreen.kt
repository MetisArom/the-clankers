package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import theclankers.tripview.data.api.ApiClient
import theclankers.tripview.ui.components.StopItem
import theclankers.tripview.ui.viewmodels.TripViewModel

@Composable
fun ItineraryScreen(navController: NavHostController, tripId: Int, viewModel: TripViewModel) {
    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    val trip by viewModel.tripState
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip?.let { "Trip #${it.tripId}" } ?: "Itinerary") },
                actions = {
                    Button(onClick = { println("Navigation clicked") }) { Text("Navigation") }
                    Button(onClick = { println("Chat clicked") }) { Text("Chat") }
                    Button(onClick = {
                        navController.navigate("EditItinerary/$tripId")
                    }) { Text("Edit") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }

            errorMessage != null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $errorMessage")
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    val stops = trip?.stops ?: emptyList()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(stops, key = { it.stopId }) { stop ->
                            StopItem(
                                stop = stop,
                                onStopClick = { clickedStop ->
                                    println("Clicked stop ${clickedStop.name}")
                                },
                                onCompletedChange = { changedStop, _ ->
                                    viewModel.toggleCompleted(changedStop)
                                }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            // call archive logic to api here
                            // navigate to trips screen after
                            // also add confirmation toast?
                            scope.launch {
                                try {
                                    ApiClient.archiveTrip(token = "user_jwt_token", tripId = tripId)
                                    println("Trip archived")
                                    navController.navigate("trips")
                                } catch (e: Exception) {
                                    println("Error archiving trip: ${e.message}")
                                }
                            }
                            viewModel.tripState.value = viewModel.tripState.value?.copy(status = "archived")
                            println("Trip archived")
                            navController.navigate("Trips")
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .width(150.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Archive Trip")
                    }
                }
            }
        }
    }
}