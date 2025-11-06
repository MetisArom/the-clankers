package theclankers.tripview.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import theclankers.tripview.ui.components.*
import theclankers.tripview.ui.theme.PurpleGrey80
import theclankers.tripview.ui.viewmodels.TripViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useSendForm

//@Composable
//fun TripFormPt2(
//    navController: NavController
//) {
//    val appVM = useAppContext();
//    val userID= appVM.userIdState.value;
//    val token= appVM.accessTokenState.value;
//    if (userID==null || token==null){
//        return;
//    }
//    Column(){
//        //Text("${appVM.latestTripResponse}")
//        Text("hey")
//    }
//}

@Composable
fun TripFormPt2(
    navController: NavController,
) {
    val appVM = useAppContext()
    val token= appVM.accessTokenState.value
    if (token==null){
        return;
    }
    val formVM= useSendForm(token)
    val isLoading = formVM.isLoadingState.value
    val errorMessage = formVM.errorMessageState.value
    val trips = formVM.tripSuggestions
    Log.d("TripFormPt2", "Fetched trip list: $trips")
    var showDialog by remember { mutableStateOf(false) }
    var selectedTripId by remember { mutableStateOf<Int?>(null)}

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $errorMessage")
            }
            return
        }
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

        items(trips) { trip ->
            Text(trip.name)
            Text(trip.description)
        }

        item {
            Spacer(Modifier.height(24.dp))
        }
    }
}
