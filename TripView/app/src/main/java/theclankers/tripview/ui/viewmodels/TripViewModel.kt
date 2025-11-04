package theclankers.tripview.ui.viewmodels

import android.R.attr.description
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import theclankers.tripview.data.models.Stop
import theclankers.tripview.data.models.Trip
import theclankers.tripview.data.models.User
import theclankers.tripview.data.network.ApiClient

// Use this ViewModel for a specific individual Trip.
// It will take as input a "trip_id" and return state variables defined in the ER diagram

class TripViewModel(private val token: String) : ViewModel() {

    val tripState: MutableState<Trip?> = mutableStateOf(null)
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val errorMessage: MutableState<String?> = mutableStateOf(null)

    fun loadTrip(tripId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val trip = ApiClient.getTrip(token, tripId)
                tripState.value = trip
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun useTrip(token: String, tripId: Int): TripViewModel {
    val viewModel = remember { TripViewModel(token) }

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    return viewModel
}