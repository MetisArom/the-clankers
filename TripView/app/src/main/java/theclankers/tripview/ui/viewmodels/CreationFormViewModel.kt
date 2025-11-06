package theclankers.tripview.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import theclankers.tripview.data.api.ApiClient
import theclankers.tripview.data.models.Trip

class SendFormViewModel(private val token: String) : ViewModel() {
    val isLoadingState = mutableStateOf(false)
    val errorMessageState = mutableStateOf<String?>(null)

    fun sendForm(destination: String, numDays: String, hotels: String, timeline: String, numChoices: String) {
        viewModelScope.launch {
            isLoadingState.value = true
            errorMessageState.value = null
            try {
                val trips = withContext(Dispatchers.IO) {
                    ApiClient.sendTripForm(token, destination, numDays, hotels, timeline, numChoices)
                }
            } catch (e: Exception) {
                errorMessageState.value = "Error loading active trips: ${e.message}"
            } finally {
                isLoadingState.value = false
            }
        }
    }
}

@Composable
fun useSendForm(token: String): SendFormViewModel {
    val viewModel = remember { SendFormViewModel(token) }
    return viewModel
}