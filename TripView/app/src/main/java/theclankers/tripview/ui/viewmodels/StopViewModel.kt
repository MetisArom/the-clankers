package theclankers.tripview.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import theclankers.tripview.data.models.Stop
import theclankers.tripview.data.api.ApiClient
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StopViewModel(private val token: String) : ViewModel() {
    // Individual fields
    val stopIdState: MutableState<Int?> = mutableStateOf(null)
    val tripIdState: MutableState<Int?> = mutableStateOf(null)
    val stopTypeState: MutableState<String?> = mutableStateOf(null)
    val latitudeState: MutableState<Double?> = mutableStateOf(null)
    val longitudeState: MutableState<Double?> = mutableStateOf(null)
    val nameState: MutableState<String?> = mutableStateOf(null)
    val completedState: MutableState<Boolean?> = mutableStateOf(null)
    val orderState: MutableState<Int?> = mutableStateOf(null)

    // UI state
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val errorMessage: MutableState<String?> = mutableStateOf(null)

    fun loadStop(stop: Stop) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {

                // ✅ Update each state field from the Stop model
                stopIdState.value = stop.stopId
                tripIdState.value = stop.tripId
                stopTypeState.value = stop.stopType
                latitudeState.value = stop.latitude
                longitudeState.value = stop.longitude
                nameState.value = stop.name
                completedState.value = stop.completed
                orderState.value = stop.order

                Log.d("StopViewModel", "✅ Stop loaded: ${stop.name}")

            } catch (e: Exception) {
                Log.e("StopViewModel", "❌ Failed to load stop", e)
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun toggleCompleted(stopId: Int, newValue: Boolean) {
        viewModelScope.launch {
            try {
                // 🔹 Send the update to the backend
                withContext(Dispatchers.IO) {
                    ApiClient.updateStopCompleted(token, stopId, newValue)
                }

                // 🔹 Update local state values
                completedState.value = newValue

                Log.d("StopViewModel", "✅ Stop completion updated to $newValue")
            } catch (e: Exception) {
                Log.e("StopViewModel", "❌ Failed to update completion", e)
                e.printStackTrace()
            }
        }
    }

    fun deleteStop(stopId: Int) {
        viewModelScope.launch {
            try {
                // 🔹 Send the update to the backend
                withContext(Dispatchers.IO) {
                    ApiClient.deleteStop(token, stopId)
                }


                Log.d("StopViewModel", "✅ Stop deleted ")
            } catch (e: Exception) {
                Log.e("StopViewModel", "❌ Failed to delete stop", e)
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun useStop(token: String, stop: Stop): StopViewModel {
    val viewModel = remember { StopViewModel(token) }

    LaunchedEffect(stop.stopId) {
        viewModel.loadStop(stop)
    }

    return viewModel
}
