package theclankers.tripview.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import theclankers.tripview.data.models.Trip
import theclankers.tripview.data.network.ApiClient

class TripViewModel(private val token: String) : ViewModel() {
    val tripIdState: MutableState<Int?> = mutableStateOf(null)
    val ownerIdState: MutableState<Int?> = mutableStateOf(null)
    val statusState: MutableState<String?> = mutableStateOf(null)
    val drivingPolylineState: MutableState<String?> = mutableStateOf(null)
    val drivingPolylineTimestampState: MutableState<String?> = mutableStateOf(null)
    val nameState: MutableState<String?> = mutableStateOf(null)
    val descriptionState: MutableState<String?> = mutableStateOf(null)
    val stopIdsState: MutableState<List<Int>?> = mutableStateOf(null)

    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val errorMessage: MutableState<String?> = mutableStateOf(null)

    fun loadTrip(tripId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val trip = ApiClient.getTrip(token, tripId)

                // ✅ Update each field from the loaded Trip
                tripIdState.value = trip.tripId
                ownerIdState.value = trip.ownerId
                statusState.value = trip.status
                drivingPolylineState.value = trip.drivingPolyline
                drivingPolylineTimestampState.value = trip.drivingPolylineTimestamp
                nameState.value = trip.name
                descriptionState.value = trip.description
                stopIdsState.value = trip.stopIds

                Log.d("TripViewModel", "✅ Trip loaded: ${trip.name}")

            } catch (e: Exception) {
                Log.e("TripViewModel", "❌ Failed to load trip", e)
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
