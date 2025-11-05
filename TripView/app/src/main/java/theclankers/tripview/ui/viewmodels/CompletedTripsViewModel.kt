package theclankers.tripview.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import theclankers.tripview.data.api.ApiClient

class CompletedTripsViewModel(private val token: String) : ViewModel() {
    val activeTripsState: MutableState<List<Int>?> = mutableStateOf(null)
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val errorMessage: MutableState<String?> = mutableStateOf(null)

    fun loadActiveTrips(userId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val trips = withContext(Dispatchers.IO) {
                    ApiClient.getCompletedTrips(token, userId)
                }
                activeTripsState.value = trips
            } catch (e: Exception) {
                errorMessage.value = "Error loading active trips: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun useCompletedTrips(token: String, userId: Int): State<List<Int>?> {
    val viewModel = remember { CompletedTripsViewModel(token) }

    LaunchedEffect(userId) {
        viewModel.loadActiveTrips(userId)
    }

    return viewModel.activeTripsState
}