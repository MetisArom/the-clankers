package theclankers.tripview.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import theclankers.tripview.ui.components.HelperText2
import theclankers.tripview.ui.theme.Purple4

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import theclankers.tripview.ui.components.HeaderText
import theclankers.tripview.ui.components.HelperText
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.utils.toast


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
            // call your existing extension:
            context.toast(msg, short = true)

            // Clear the message to avoid repeated toasts
            appVM.clearToastMessage()
        }
    }

    if (token == null) return

    if (appVM.isLoadingState.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (appVM.errorMessageState.value != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Error: ${appVM.errorMessageState.value}")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            HeaderText(text = "Pick Itinerary")
            HelperText(text = "Please select one of the following itineraries:")
            Spacer(Modifier.height(8.dp))
        }

        items(tripSuggestions) { trip ->
            // This needs to be a box around the variables:
            // trip.name, trip.description
            // There needs to be a function called appVM.chooseTrip(trip, navController) {}
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        appVM.chooseTrip(trip, navController)
                    },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Purple4.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = trip.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
        }
    }
}