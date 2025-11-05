package theclankers.tripview.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import theclankers.tripview.data.models.Stop

class ItineraryViewModel : ViewModel() {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val _stops = MutableStateFlow<List<Stop>>(emptyList())
    var stops: StateFlow<List<Stop>> = _stops

    fun loadStops(tripId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    // should be changed to wherever server is running
                    .url("http://127.0.0.1:8080/trips/$tripId/")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val stopsList = json.decodeFromString<List<Stop>>(responseBody)
                            _stops.value = stopsList
                        }
                    } else {
                        println("HTTP error: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleCompleted(stop: Stop) {
        _stops.update { currentList ->
            currentList.map { currentStop ->
                if (currentStop.stopId == stop.stopId) currentStop.copy(completed = !currentStop.completed)
                else currentStop
            }
        }

    }
}
