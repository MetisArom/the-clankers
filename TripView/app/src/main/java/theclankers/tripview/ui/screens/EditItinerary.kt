package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import theclankers.tripview.data.models.Stop
import theclankers.tripview.data.api.ApiClient
import theclankers.tripview.ui.components.StopItem
import theclankers.tripview.ui.viewmodels.TripViewModel

@Composable
fun EditItinerary(navController: NavHostController) { // , tripId: Int?, viewModel: TripViewModel
    // sample data hardcoded
    var stops by remember {
        mutableStateOf(
            listOf(
                Stop(1, 1, "pickup", 37.8199, -122.4783, "Morning walk across the bridge", true, 0),
                Stop(2, 1, "dropoff", 37.8080, -122.4177, "Seafood lunch by the bay", false, 1),
                Stop(3,1, "pickup", 37.8267, -122.4230, "Afternoon tour of the historic prison", true, 2),
                Stop(4, 1, "dropoff", 37.7544, -122.4477, "Sunset view over San Francisco", false, 3),
                Stop(5, 1, "pickup", 37.8267, -122.4230, "Afternoon tour of the historic prison", true, 4),
                Stop(6, 1, "dropoff", 37.8267, -122.4230, "Afternoon tour of the historic prison", false, 5),
                Stop(7, 1, "pickup", 37.8267, -122.4230, "Afternoon tour of the historic prison", true, 6),
                Stop(8, 1, "dropoff", 37.8267, -122.4230,  "Afternoon tour of the historic prison", false, 7),
                Stop(9, 1, "pickup", 37.8267, -122.4230, "Afternoon tour of the historic prison", true, 8),
                Stop(10, 1, "dropoff", 37.8267, -122.4230, "Afternoon tour of the historic prison", false, 9)
            )
        )
    }

    // LaunchedEffect(tripId) {
    //     tripId?.let { viewModel.loadTrip(it) }
    // }

    // val trip by viewModel.tripState
    // var stops = trip?.stops ?: emptyList()

    // val lazyListState = rememberLazyListState()
    // val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
    //     // Update the list
    //     stops = stops.toMutableList().apply {
    //         add(to.index, removeAt(from.index))
    //     }
    // }

    Scaffold(
        topBar = {
            TopAppBar(
                // should be based on whatever trip object is passed into this
                // TODO: current placeholder, need to add trip names to db later
                // title = { Text(trip?.let { "Trip #${it.tripId}" } ?: "Itinerary") },
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
                    .padding(16.dp)/*,
                state = lazyListState*/
            ) {
                items(stops) { stop ->
                    StopItem(stopId = stop.stopId, navController = navController)
                }
            }

            //    items(stops, key = { it.stopId }) { stop ->
            //        ReorderableItem(reorderableLazyListState, key = stop.stopId) { isDragging ->
            //            Box(
            //                modifier = Modifier
            //                    .fillMaxWidth()
            //                    .padding(vertical = 4.dp)
            //                    .longPressDraggableHandle()
            //            ) {
            //                StopItem(
            //                    stop = stop,
            //                    onStopClick = { println("Hello") },
            //                    onCompletedChange = { stopChanged, completed ->
            //                        stops = stops.map {
            //                            if (it.stopId == stopChanged.stopId) it.copy(completed = completed) else it
            //                        }
            //                    },
            //                    onDeleteStop = { stopToDelete ->
            //                        try {
            //                            // api call
            //                            val response = ApiClient.deleteStop(token = "user_jwt_token", tripId = 1, stopId = stopToDelete.stopId)
            //                            println("Delete response: $response")

            //                            stops = stops.filter { it.stopId != stopToDelete.stopId }
            //                        } catch (e: Exception) {
            //                            e.printStackTrace()
            //                        }
            //                    },
            //                    editMode = true,
            //                    trailingContent = {
            //                        Icon(
            //                            imageVector = Icons.Rounded.DragHandle,
            //                            contentDescription = "Reorder",
            //                            modifier = Modifier.longPressDraggableHandle()

            //                        )
            //                    }
            //                )
            //            }

            //        }

            //    }
            Button(
                onClick = {
                    // call archive logic to api here
                    // navigate to trips screen after
                    // also add confirmation toast?
                    // println("Trip archived")
                    println("Confirmed changes")
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .width(150.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Confirm changes")
                // Text("Archive Trip")
            }
        }
    }
}
}