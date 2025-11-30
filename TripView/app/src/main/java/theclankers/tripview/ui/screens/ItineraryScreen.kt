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
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.viewmodels.TripViewModel
import theclankers.tripview.ui.viewmodels.useTrip

@Composable
fun ItineraryScreen(navController: NavHostController, tripId: Int, token: String) {
    val viewModel = useTrip(token, tripId)
    val tripIdState by viewModel.tripIdState
    val nameState by viewModel.nameState
    val stopIds by viewModel.stopIdsState
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val stops by viewModel.stops

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nameState ?: "") },
                actions = {
                    Button(onClick = {
                        navigateToDetail(navController, "navigation/$tripId" )
                    }) { Text("Navigation") }
                    Button(onClick = {
                        navigateToDetail(navController, "chat" )
                    }) { Text("Chat") }
                    Button(onClick = { navController.navigate("EditItinerary/$tripId") }) { Text("Edit") }
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
            ) {
                items(stops ?: emptyList(), key = { it.stopId }) { stop ->
                    StopItem(navController = navController, stop)
                }
            }

            Button(
                onClick = {
                    // call archive logic to api here
                    // navigate to trips screen after
                    // also add confirmation toast?
                    viewModel.archiveTrip(tripId)
                    println("Trip archived")
                    navController.navigate("trips") {
                        popUpTo("trips") { inclusive = true }
                        launchSingleTop = true
                    }
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