package theclankers.tripview.ui.screens

import android.R.attr.text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.data.models.Trip
import theclankers.tripview.data.models.TripSuggestion
import theclankers.tripview.ui.components.HeaderText
import theclankers.tripview.ui.components.HelperText
import theclankers.tripview.ui.theme.Purple4
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.utils.toast
//import androidx

@Composable
fun TripFormPt2(navController: NavHostController) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value
    val tripSuggestions = appVM.tripSuggestionsState.value

    val context = LocalContext.current

    // Observe the toast message
    val toastMsg by remember { derivedStateOf { appVM.toastMessage.value } }
    LaunchedEffect(toastMsg) {
        toastMsg?.let { msg ->
            context.toast(msg, short = true)
            appVM.clearToastMessage()
        }
    }

    if (token == null) return

    if (appVM.isLoadingState.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
        return
    }

    if (appVM.errorMessageState.value != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { Text("Error: ${appVM.errorMessageState.value}") }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            HeaderText(text = "Pick Your Adventure")
            HelperText(text = "Select one of the curated itineraries below. Tap to see more details!")
            Spacer(Modifier.height(16.dp))
        }

        items(tripSuggestions) { trip ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { expanded = !expanded },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Purple4.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header row with trip name and toggle icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = trip.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = trip.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    // New fields
                    Text(
                        text = "Total Cost Estimate: $${trip.totalCostEstimate}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Transportation Summary:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = trip.transportationSummary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Transportation Breakdown:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = trip.transportationBreakdown,
                        style = MaterialTheme.typography.bodySmall
                    )

                    AnimatedVisibility(
                        visible = expanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            trip.stops.forEach { stop ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Purple4.copy(alpha = 0.05f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "${stop.order}. ${stop.name} (${stop.stopType})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Purple4
                                        )
                                        Spacer(Modifier.height(4.dp))
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = { appVM.chooseTrip(trip, navController) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Select This Trip")
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
        }
    }
}
