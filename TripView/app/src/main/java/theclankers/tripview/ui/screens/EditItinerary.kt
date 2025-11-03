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

@Composable
fun EditItinerary(navController: NavHostController, tripId: Int?) {
    // sample data hardcoded
    var stops by remember {
        mutableStateOf(
            listOf(
                Stop(1, 37.8199, -122.4783, 0, "Morning walk across the bridge", "Bridge", 0, completed = true, "test"),
                Stop(2, 37.8080, -122.4177, 0, "Seafood lunch by the bay", "Bay", 0, completed = false, "test"),
                Stop(3,37.8267, -122.4230, 0, "Afternoon tour of the historic prison", "Prison", 0, completed = false, "test"),
                Stop(4, 37.7544, -122.4477, 0, "Sunset view over San Francisco", "Mountain", 0, completed = true, "test"),
                Stop(5, 37.8267, -122.4230, 0, "Afternoon tour of the historic prison", "Prison", 0, completed = false, "test"),
                Stop(6, 37.8267, -122.4230, 0, "Afternoon tour of the historic prison", "Prison", 0, completed = false, "test"),
                Stop(7, 37.8267, -122.4230, 0, "Afternoon tour of the historic prison", "Prison", 0, completed = false, "test"),
                Stop(8, 37.8267, -122.4230, 0, "Afternoon tour of the historic prison", "Prison", 0, completed = false, "test"),
                Stop(9, 37.8267, -122.4230, 0, "Afternoon tour of the historic prison", "Prison", 0, completed = false, "test"),
                Stop(10, 37.8267, -122.4230, 0, "Afternoon tour of the historic prison", "Prison", 0, completed = false, "test")
            )
        )
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Update the list
        stops = stops.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // should be based on whatever trip object is passed into this
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
                    .padding(16.dp),
                state = lazyListState
            ) {
                items(stops, key = { it.stopId }) { stop ->
                    ReorderableItem(reorderableLazyListState, key = stop.stopId) { isDragging ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
//                                .longPressDraggableHandle()
                        ) {
                            StopItem(
                                stop = stop,
                                onStopClick = { println("Hello") },
                                onCompletedChange = { stopChanged, completed ->
                                    stops = stops.map {
                                        if (it.stopId == stopChanged.stopId) it.copy(completed = completed) else it
                                    }
                                },
                                onDeleteStop = { stopToDelete ->
                                    try {
                                        // api call
                                        val response = ApiClient.deleteStop(token = "user_jwt_token", tripId = 1, stopId = stopToDelete.stopId)
                                        println("Delete response: $response")

                                        stops = stops.filter { it.stopId != stopToDelete.stopId }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                editMode = true,
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.DragHandle,
                                        contentDescription = "Reorder",
                                        modifier = Modifier.longPressDraggableHandle()

                                    )
                                }
                            )
                        }

                    }

                }

            }
            Button(
                onClick = {
                    println("Confirmed changes")
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .width(150.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Confirm changes")
            }
        }
    }
}