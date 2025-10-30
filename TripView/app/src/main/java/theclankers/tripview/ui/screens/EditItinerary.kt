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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.classes.Stop
import theclankers.tripview.ui.components.StopItem

@Composable
fun EditItinerary(navController: NavHostController) {
    // sample data hardcoded
    var stops by remember {
        mutableStateOf(
            listOf(
                Stop(1, 37.8199, -122.4783, "Morning walk across the bridge", completed = true),
                Stop(2, 37.8080, -122.4177, "Seafood lunch by the bay", completed = false),
                Stop(
                    3,
                    37.8267,
                    -122.4230,
                    "Afternoon tour of the historic prison",
                    completed = false
                ),
                Stop(4, 37.7544, -122.4477, "Sunset view over San Francisco", completed = true),
                Stop(
                    5,
                    37.8267,
                    -122.4230,
                    "Afternoon tour of the historic prison",
                    completed = false
                ),
                Stop(
                    6,
                    37.8267,
                    -122.4230,
                    "Afternoon tour of the historic prison",
                    completed = false
                ),
                Stop(
                    7,
                    37.8267,
                    -122.4230,
                    "Afternoon tour of the historic prison",
                    completed = false
                ),
                Stop(
                    8,
                    37.8267,
                    -122.4230,
                    "Afternoon tour of the historic prison",
                    completed = false
                ),
                Stop(
                    9,
                    37.8267,
                    -122.4230,
                    "Afternoon tour of the historic prison",
                    completed = false
                ),
                Stop(
                    10,
                    37.8267,
                    -122.4230,
                    "Afternoon tour of the historic prison",
                    completed = false
                )

            )
        )
    }

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
                    .padding(16.dp)
            ) {
                items(stops) { stop ->
                    StopItem(
                        stop = stop,
                        onStopClick = { println("Hello") },
                        onCompletedChange = { stop, completed ->
                            stops = stops.map {
                                if (it.id == stop.id) it.copy(completed = completed) else it
                            }
                        },
                        editMode = true
                    )
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