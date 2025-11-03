package theclankers.tripview.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import theclankers.tripview.data.models.User
import theclankers.tripview.data.api.ApiClient
import androidx.compose.runtime.State

// Use this ViewModel for grabbing state relevant to a specific user.
// For example, pass as input "user_id" and it will return variables like "first_name", "last_name", and "username"

class UserViewModel(private val token: String) : ViewModel() {

    val userState: MutableState<User?> = mutableStateOf(null)
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val errorMessage: MutableState<String?> = mutableStateOf(null)

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val responseString = ApiClient.getUser(token, userId)
                val json = JSONObject(responseString)
                val user = User(
                    userId = json.getInt("user_id"),
                    username = json.getString("username"),
                    firstName = json.getString("firstname"),
                    lastName = json.getString("lastname"),
                    likes = json.optString("likes", null),
                    dislikes = json.optString("dislikes", null)
                )
                userState.value = user
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun useUser(token: String, userId: Int): State<User?> {
    val viewModel = remember { UserViewModel(token) }

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    return viewModel.userState
}
