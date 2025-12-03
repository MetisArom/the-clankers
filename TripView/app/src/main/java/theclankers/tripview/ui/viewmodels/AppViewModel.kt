package theclankers.tripview.ui.viewmodels

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.json.JSONObject
import theclankers.tripview.data.models.User
import theclankers.tripview.data.api.ApiClient
import theclankers.tripview.data.models.LoginResult
import theclankers.tripview.data.models.TripSuggestion
import theclankers.tripview.ui.navigation.goBack
import theclankers.tripview.ui.navigation.navigateToRoot

class AppViewModel : ViewModel() {
    // Explicit MutableState variables with consistent naming
    val userIdState = mutableStateOf<Int?>(null)
    val accessTokenState = mutableStateOf<String?>(null)
    val isAuthedState = mutableStateOf(false)
    val authErrorMessageState = mutableStateOf<String?>(null)
    val showNavbarState = mutableStateOf(false)
    val isAuthingState = mutableStateOf(false)
    val demoFlowState = mutableStateOf(false)

    val tripSuggestionsState = mutableStateOf<List<TripSuggestion>>(emptyList())
    val destination = mutableStateOf("")
    val numDays = mutableStateOf("1")
    val stops = mutableStateOf("")
    val timeline = mutableStateOf("")
    val numChoices = mutableStateOf("3")

    val toastMessage: MutableState<String?> = mutableStateOf(null)
    val isLoadingState = mutableStateOf(false)
    val errorMessageState = mutableStateOf<String?>(null)

    fun submitForm(navController: NavController) {
        errorMessageState.value = null

        if (accessTokenState.value == null) {
            Log.e("AppViewModel", "submitForm failed: accessToken is null")
            errorMessageState.value = "Access token is null"
            return
        }
        
        viewModelScope.launch {
            try {
                isLoadingState.value = true
                tripSuggestionsState.value = ApiClient.submitForm(accessTokenState.value!!, destination.value, numDays.value, stops.value, timeline.value, numChoices.value)
            } catch (e: Exception) {
                Log.e("AppViewModel", "submitForm failed", e)
                errorMessageState.value = e.message
            } finally {
                errorMessageState.value = null
                Log.d("AppViewModel", "submitForm completed")
                navController.navigate("TripFormPt2")
                isLoadingState.value = false
            }
        }
    }

    fun chooseTrip(trip: TripSuggestion, navController: NavHostController) {
        errorMessageState.value = null

        if (accessTokenState.value == null) {
            Log.e("AppViewModel", "chooseTrip failed: accessToken is null")
            errorMessageState.value = "Access token is null"
            return
        }

        viewModelScope.launch {
            try {
                isLoadingState.value = true
                // Package the trip as a JSON object and send to the backend
                val tripJson = JSONObject().apply {
                    put("name", trip.name)
                    put("description", trip.description)
                    put("stops", trip.stops)
                }

                ApiClient.chooseTrip(accessTokenState.value!!, tripJson)
                toastMessage.value = "Trip '${trip.name}' created successfully."
            } catch (e: Exception) {
                Log.e("AppViewModel", "chooseTrip failed", e)
                toastMessage.value = "Failed to create trip: ${e.message ?: "unknown error"}"
                errorMessageState.value = e.message
            } finally {
                isLoadingState.value = false
                goBack(navController)
                goBack(navController)
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            demoFlowState.value = false
            isAuthingState.value = true
            authErrorMessageState.value = null
            try {
                val loginResult: LoginResult = ApiClient.login(username, password)

                userIdState.value = loginResult.userId
                accessTokenState.value = loginResult.accessToken
                isAuthedState.value = true

                showNavbarState.value = true

                Log.d("AppViewModel", "✅ Login success, user_id=$loginResult.userId")

            } catch (e: Exception) {
                authErrorMessageState.value = e.message
                isAuthedState.value = false
                Log.e("AppViewModel", "❌ Login failed: ${e.message}")
            } finally {
                isAuthingState.value = false
            }
        }
    }

    fun logout() {
        userIdState.value = null
        accessTokenState.value = null
        isAuthedState.value = false
        showNavbarState.value = false
        Log.d("AppViewModel", "🔒 Logged out")
    }

    // TODO: Implement signup function, which is called from the SignupScreen and calls ApiService.signup
    fun signup(username: String, email: String, password: String, firstName: String, lastName: String, likes: String, dislikes: String) {

    }

    fun toggleNavbar() {
        showNavbarState.value = !showNavbarState.value
    }

    fun clearToastMessage() {
        toastMessage.value = null
    }

    // Map from tripId → current UI stopIds (editable list)
    val uiStopIdsPerTrip: MutableState<Map<Int, List<Int>>> = mutableStateOf(emptyMap())

    // Helper functions to access / update UI stopIds
    fun getUiStopIds(tripId: Int): List<Int> {
        return uiStopIdsPerTrip.value[tripId] ?: emptyList()
    }

    fun setUiStopIds(tripId: Int, stopIds: List<Int>) {
        uiStopIdsPerTrip.value = uiStopIdsPerTrip.value.toMutableMap().apply {
            put(tripId, stopIds)
        }
    }

    fun deleteStop(tripId: Int, stopId: Int) {
        val current = getUiStopIds(tripId)
        setUiStopIds(tripId, current.filter { it != stopId })
    }

    fun syncInitialUiStopIds(tripId: Int, stopIds: List<Int>) {
        if (uiStopIdsPerTrip.value[tripId].isNullOrEmpty()) {
            setUiStopIds(tripId, stopIds)
        }
    }
}

@Composable
fun useAppContext(): AppViewModel {
    // Gives the same global ViewModel across screens
    return viewModel(LocalActivity.current as ComponentActivity)
}
