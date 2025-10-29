package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.classes.Stop
import theclankers.tripview.ui.components.StopItem

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(stops) { stop ->
                StopItem(
                    stop = stop,
                    onStopClick = { clickedStop ->
                        // navigate or show details
                        println("Hello")
                    },
                    onCompletedChange = { updatedStop, isChecked ->
                        // update stop in list/viewmodel
                        println("Stop ${updatedStop.name} marked as $isChecked")
                    }
                )
            }
        }
    }
}