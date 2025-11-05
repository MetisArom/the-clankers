package theclankers.tripview.ui.viewmodels

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import theclankers.tripview.data.network.ApiClient

class AppViewModel : ViewModel() {
    // Explicit MutableState variables with consistent naming
    val userIdState = mutableStateOf<Int?>(null)
    val accessTokenState = mutableStateOf<String?>(null)
    val isAuthedState = mutableStateOf(false)
    val authErrorMessageState = mutableStateOf<String?>(null)
    val showNavbarState = mutableStateOf(true)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            authErrorMessageState.value = null
            try {
                val responseString = ApiClient.loginUser(username, password)
                val json = JSONObject(responseString)

                // Parse user_id and access_token from backend response
                val returnedUserId = json.getInt("user_id")
                val returnedAccessToken = json.getString("access_token")

                userIdState.value = returnedUserId
                accessTokenState.value = returnedAccessToken
                isAuthedState.value = true

                Log.d("AppViewModel", "✅ Login success, user_id=$returnedUserId")

            } catch (e: Exception) {
                authErrorMessageState.value = e.message
                isAuthedState.value = false
                Log.e("AppViewModel", "❌ Login failed: ${e.message}")
            }
        }
    }

    fun logout() {
        userIdState.value = null
        accessTokenState.value = null
        isAuthedState.value = false
        Log.d("AppViewModel", "🔒 Logged out")
    }

    fun toggleNavbar() {
        showNavbarState.value = !showNavbarState.value
    }
}

@Composable
fun useAppContext(): AppViewModel {
    // Gives the same global ViewModel across screens
    return viewModel(LocalActivity.current as ComponentActivity)
}
