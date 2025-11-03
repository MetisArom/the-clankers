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

// This file stores the global context that will be used throughout the entire app.
// It's not implemented yet...
class AppViewModel : ViewModel() {
    val authedUserState: MutableState<User?> = mutableStateOf(null)
    val isAuthed: MutableState<Boolean> = mutableStateOf(false)
    val authErrorMessage: MutableState<String?> = mutableStateOf(null)
    val authAccessToken: MutableState<String> = mutableStateOf("")

    fun login(username: String, password: String) {
        viewModelScope.launch {
            authErrorMessage.value = null
            try {
                val responseString = ApiClient.loginUser(username, password)
                val json = JSONObject(responseString)
                authAccessToken.value = json.getString("access_token")
                val userJson = json.getJSONObject("user")
                val user = User(
                    userId = userJson.getInt("id"),
                    username = userJson.getString("username"),
                    firstName = userJson.getString("firstname"),
                    lastName = userJson.getString("lastname"),
                    likes = userJson.optString("likes", null),
                    dislikes = userJson.optString("dislikes", null)
                )
                authedUserState.value = user
                isAuthed.value = true
                Log.d(null, "Successful login as ${user.userId}")
            } catch (e: Exception) {
                authErrorMessage.value = e.message
                Log.d(null, "Failed login: ${e.message}")
            }
        }
    }

    var showNavbar by mutableStateOf(true)
        private set

    fun toggleNavbar() {
        showNavbar = !showNavbar
    }
}

@Composable
fun getAuthedUser(): State<User?> {
    val activityVM: AppViewModel = viewModel(LocalActivity.current as ComponentActivity)

    return activityVM.authedUserState
}