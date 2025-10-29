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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.classes.Stop
import theclankers.tripview.ui.components.StopItem

@Composable
fun SampleTrip(navController: NavHostController) {
    // sample data hardcoded
    var stops by remember { mutableStateOf(
        listOf(
        Stop(1, 37.8199, -122.4783, "Morning walk across the bridge", completed = true),
        Stop(2, 37.8080, -122.4177, "Seafood lunch by the bay", completed = false),
        Stop(3, 37.8267, -122.4230, "Afternoon tour of the historic prison", completed = false),
        Stop(4, 37.7544, -122.4477, "Sunset view over San Francisco", completed = true),
        Stop(5, 37.8267, -122.4230, "Afternoon tour of the historic prison", completed = false),
        Stop(6, 37.8267, -122.4230, "Afternoon tour of the historic prison", completed = false),
        Stop(7, 37.8267, -122.4230, "Afternoon tour of the historic prison", completed = false),
        Stop(8, 37.8267, -122.4230, "Afternoon tour of the historic prison", completed = false),
        Stop(9, 37.8267, -122.4230, "Afternoon tour of the historic prison", completed = false),
        Stop(10, 37.8267, -122.4230, "Afternoon tour of the historic prison", completed = false)

        )
    )}

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
                    onCompletedChange = { stop, completed ->
                        // update stop if checked
                        stops = stops.map {
                            if (it.id == stop.id) it.copy(completed = completed) else it
                        }
                    }
                )
            }
        }
    }
}