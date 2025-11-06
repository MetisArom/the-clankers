package theclankers.tripview.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import theclankers.tripview.data.api.ApiClient
import theclankers.tripview.data.models.Trip
import theclankers.tripview.data.models.TripSuggestion

class SendFormViewModel(private val token: String) : ViewModel() {

    val tripSuggestions = mutableStateListOf<TripSuggestion>()
    val isLoadingState = mutableStateOf(false)
    val errorMessageState = mutableStateOf<String?>(null)

    fun sendForm(destination: String, numDays: String, stops: String, timeline: String, numChoices: String) {
        viewModelScope.launch {
            isLoadingState.value = true
            errorMessageState.value = null

            try {
                val results = withContext(Dispatchers.IO) {
                    ApiClient.sendTripForm(token, destination, numDays, stops, timeline, numChoices)
                }
                // update the existing snapshot list so Compose sees the change:
                tripSuggestions.clear()
                tripSuggestions.addAll(results)
            } catch (e: Exception) {
                errorMessageState.value = "Error loading trip suggestions: ${e.message}"
                Log.e("SendFormVM", "sendForm failed", e)
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