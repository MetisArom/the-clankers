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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.data.models.Stop
import theclankers.tripview.ui.components.StopItem
import kotlin.collections.map
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import theclankers.tripview.data.api.ApiClient

@Composable
fun ItineraryScreen(navController: NavHostController, tripId: Int, viewModel: ItineraryViewModel) {
    LaunchedEffect(tripId) {
        viewModel.loadStops(tripId)
    }

    val stops by viewModel.stops.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("San Francisco Itinerary") },
                actions = {
                    Button(onClick = { println("Navigation clicked") }) { Text("Navigation") }
                    Button(onClick = { println("Chat clicked") }) { Text("Chat") }
                    Button(onClick = { println("Edit clicked") }) { Text("Edit") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(stops, key = { it.stopId }) { stop ->
                    StopItem(navController, stop.stopId)
                }
            }

            Button(
                onClick = {
                    // call archive logic to api here
                    // navigate to trips screen after
                    // also add confirmation toast?
                    println("Trip archived")
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