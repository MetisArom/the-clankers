package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import theclankers.tripview.classes.Stop
import theclankers.tripview.ui.components.StopItem

@Composable
fun ItineraryScreen(
    tripId: Int,
    viewModel: ItineraryViewModel,
    onStopClick: (Stop) -> Unit
) {
    val stops by viewModel.stops.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.loadStops(tripId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Itinerary for Trip #$tripId") })
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
                    onStopClick = { onStopClick(stop) },
                    onCompletedChange = { _, _ -> } // disabled since it's read-only
                )
            }
        }
    }
}
