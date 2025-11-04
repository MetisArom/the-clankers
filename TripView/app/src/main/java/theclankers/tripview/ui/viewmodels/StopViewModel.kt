package theclankers.tripview.ui.viewmodels

import theclankers.tripview.data.models.Trip

// Use this ViewModel to grab data related to an individaul Stop
// Pass as input a "stop_id" and it will give access to all relevant stop object varaibles according to the ER diagram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import theclankers.tripview.data.models.Stop
import theclankers.tripview.data.network.ApiClient

class StopViewModel(private val token: String) : ViewModel() {

    val stopState: MutableState<Stop?> = mutableStateOf(null)
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val errorMessage: MutableState<String?> = mutableStateOf(null)

    fun loadStop(stopId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val stop = ApiClient.getStop(token, stopId)
                stopState.value = stop
            } catch (e: Exception) {
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
                ApiClient.updateStopCompleted(token, stopId, newValue)

                // 🔹 Update local state
                stopState.value = stopState.value?.copy(completed = newValue)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun useStop(token: String, stopId: Int): StopViewModel {
    val viewModel = remember { StopViewModel(token) }

    LaunchedEffect(stopId) {
        viewModel.loadStop(stopId)
    }

    return viewModel
}