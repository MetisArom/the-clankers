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
import kotlinx.coroutines.launch
import org.json.JSONObject
import theclankers.tripview.data.models.User
import theclankers.tripview.data.api.ApiClient
import theclankers.tripview.data.models.LoginResult

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
                val loginResult: LoginResult = ApiClient.login(username, password)

                userIdState.value = loginResult.userId
                accessTokenState.value = loginResult.accessToken
                isAuthedState.value = true

                Log.d("AppViewModel", "✅ Login success, user_id=$loginResult.userId")

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

    // TODO: Implement signup function, which is called from the SignupScreen and calls ApiService.signup
    fun signup(username: String, email: String, password: String, firstName: String, lastName: String, likes: String, dislikes: String) {

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
