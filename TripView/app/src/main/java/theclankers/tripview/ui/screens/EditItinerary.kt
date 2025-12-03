package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import theclankers.tripview.data.api.ApiClient.deleteStop
import theclankers.tripview.ui.components.EditableStopItem
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useTrip

@Composable
fun EditItinerary(navController: NavHostController, tripId: Int) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value
    if (token == null) return

    val tripVM = useTrip(token, tripId)
    val nameState by tripVM.nameState
    val stopIds by tripVM.stopIdsState

    // Get uiStopIds from global AppViewModel
    val uiStopIds = appVM.getUiStopIds(tripId)

    LaunchedEffect(stopIds) {
        tripVM.loadTrip(tripId)          // Load from backend
        appVM.syncInitialUiStopIds(tripId, stopIds ?: emptyList())  // Sync UI state once
    }

    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val list = uiStopIds.toMutableList()
        list.add(to.index, list.removeAt(from.index))
        appVM.setUiStopIds(tripId, list)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nameState ?: "Trip #$tripId") },
                actions = {
                    Button(onClick = {
                        navigateToDetail(navController, "invites/$tripId")
                    }) { Text("Invites") }

                    Button(onClick = {
                        navigateToDetail(navController, "addStop/$tripId")
                    }) { Text("Add Stop") }

                    // Revert Changes button
                    Button(onClick = {
                        stopIds?.let { originalStopIds ->
                            appVM.setUiStopIds(tripId, originalStopIds)
                        }
                    }) {
                        Text("Revert")
                    }
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
                modifier = Modifier.fillMaxSize().padding(16.dp),
                state = lazyListState
            ) {
                items(uiStopIds, key = { it }) { stopId ->
                    ReorderableItem(reorderState, key = stopId) { _ ->

                        EditableStopItem(
                            navController = navController,
                            stopId = stopId,
                            onDeleteStop = { deleteId ->
                                appVM.deleteStop(tripId, deleteId)   // <-- new function
                            },
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

            Button(
                onClick = {
                    tripVM.updateTrip(tripId, uiStopIds)
                    navController.navigate("ItineraryScreen/$tripId") {
                        popUpTo("ItineraryScreen/$tripId") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .width(150.dp)
            ) {
                Text("Confirm changes")
            }
        }
    }
}
