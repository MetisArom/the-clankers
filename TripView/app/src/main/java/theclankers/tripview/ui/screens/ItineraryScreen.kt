package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.ui.components.StopItem
import theclankers.tripview.ui.components.TitleText
import theclankers.tripview.ui.navigation.navigateToDetail
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
                title = { TitleText(nameState ?: "") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { navigateToDetail(navController, "navigation/$tripId") },
                    colors= ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D))) {
                    Text("Navigation")
                }
                Button(onClick = { navigateToDetail(navController, "chat") },
                    colors= ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D))) {
                    Text("Chat")
                }
                Button(onClick = { navController.navigate("EditItinerary/$tripId") },
                    colors= ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D))) {
                    Text("Edit")
                }
            }

            Spacer(Modifier.height(1.dp))
            Box(modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)) {

                LazyColumn(Modifier.fillMaxSize()
                    //.padding(padding)
                ) {
                    items(stops ?: emptyList(), key = { it.stopId }) { stop ->
                        StopItem(navController = navController, stop)
                    }
                }

                Button(
                    onClick = {
                        viewModel.archiveTrip(tripId)
                        navController.navigate("trips") {
                            popUpTo("trips") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .width(150.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors= ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D))
                ) {
                    Text("Archive Trip")
                }
            }
        }
    }
}