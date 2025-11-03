package theclankers.tripview.ui.viewmodels

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
import theclankers.tripview.data.api.ApiClient

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
                val infoResponse = ApiClient.getTrip(token, tripId)
                val infoJson = JSONObject(infoResponse)
                val trip = Trip(
                    tripId = infoJson.getInt("trip_id"),
                    ownerId = infoJson.getInt("owner_id"),
                    status = infoJson.getString("status"),
                    drivingPolyline = infoJson.getString("driving_polyline"),
                    stops = emptyList()
                )
                tripState.value = trip

                Log.d(null,"tripState value updated!")

                val stopResponse = ApiClient.getTripStops(token, tripId)
                val stopjson = JSONArray(stopResponse)
                val stops = mutableListOf<Stop>()

                for (i in 0 until stopjson.length()) {
                    val stopObject = stopjson.getJSONObject(i)
                    stops.add(
                        Stop(
                            stopId = stopObject.getInt("stop_id"),
                            latitude = stopObject.getDouble("latitude"),
                            longitude = stopObject.getDouble("longitude"),
                            description = stopObject.getString("description"),
                            order = stopObject.getInt("stop_order"),
                            completed = stopObject.getBoolean("completed"),
                            stopType = stopObject.getString("stop_type"),
                            name = "PLACEHOLDER",
                            tripId = tripId
                        )
                    )
                }

                val tripWithStops = Trip(
                    tripId = infoJson.getInt("trip_id"),
                    ownerId = infoJson.getInt("owner_id"),
                    status = infoJson.getString("status"),
                    drivingPolyline = infoJson.getString("driving_polyline"),
                    stops = stops
                )
                tripState.value = tripWithStops

                Log.d(null,"tripState value updated with stops!")
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun toggleCompleted(stop: Stop) {
        val currentTrip = tripState.value ?: return

        // Create an updated list of stops
        val updatedStops = currentTrip.stops.map { currentStop ->
            if (currentStop.stopId == stop.stopId) {
                currentStop.copy(completed = !currentStop.completed)
            } else currentStop
        }

        tripState.value = currentTrip.copy(stops = updatedStops)
    }

}

@Composable
fun useTrip(token: String, tripId: Int): State<Trip?> {
    val viewModel = remember { TripViewModel(token) }

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    return viewModel.tripState
}