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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import theclankers.tripview.data.models.Stop
import theclankers.tripview.data.api.ApiClient
import theclankers.tripview.ui.components.StopItem
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.viewmodels.TripViewModel
import theclankers.tripview.ui.viewmodels.useStop
import theclankers.tripview.ui.viewmodels.useTrip

@Composable
fun EditItinerary(navController: NavHostController, tripId: Int, token: String) { // , tripId: Int?, viewModel: TripViewModel
    val viewModel = useTrip(token, tripId)
    val tripIdState by viewModel.tripIdState
    val nameState by viewModel.nameState
    val stopIds by viewModel.stopIdsState
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

//    val stops = remember { mutableStateOf<List<Stop>>(viewModel.stops.value?: emptyList()) }
    val stops = viewModel.stops


    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Update the list
        val item = stops.removeAt(from.index)
        stops.add(to.index, item)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // should be based on whatever trip object is passed into this
                title = { Text(viewModel.nameState.value?: "Trip #$tripId") },
                actions = {
                    Button(onClick = { println("Navigation clicked") }) { Text("Navigation") }
                    Button(onClick = { println("Chat clicked") }) { Text("Chat") }
                    Button(onClick = { navigateToDetail(navController, "addStop/$tripId") }) { Text("Add Stop") }
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
                        ) {
                            StopItem(
                                navController = navController,
                                stop = stop,
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.DragHandle,
                                        contentDescription = "Reorder",
                                        modifier = Modifier.longPressDraggableHandle()

                                    )
                                },
                                onDeleteStop = { deleteId ->
                                    viewModel.deleteStop(deleteId)
                                    stops.removeAll { it.stopId == deleteId }

                                },
                                editMode = true
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    // call confirm changes to api here
                    // navigate to trips screen after
                    // also add confirmation toast?
                    viewModel.updateTrip(tripId, stops)
                    println("Confirmed changes")
                    navController.navigate("ItineraryScreen/$tripId")
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
