package theclankers.tripview.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import theclankers.tripview.data.network.ApiClient

class UserViewModel(private val token: String) : ViewModel() {
    // Expose individual states
    val firstNameState = mutableStateOf<String?>(null)
    val lastNameState = mutableStateOf<String?>(null)
    val usernameState = mutableStateOf<String?>(null)
    val emailState = mutableStateOf<String?>(null)
    val likesState = mutableStateOf<String?>(null)
    val dislikesState = mutableStateOf<String?>(null)

    val isLoadingState = mutableStateOf(false)
    val errorMessageState = mutableStateOf<String?>(null)

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            isLoadingState.value = true
            errorMessageState.value = null
            try {
                val user = ApiClient.getUser(token, userId)

                // Assign to individual fields
                firstNameState.value = user.firstName
                lastNameState.value = user.lastName
                usernameState.value = user.username
                emailState.value = user.email
                likesState.value = user.likes
                dislikesState.value = user.dislikes

            } catch (e: Exception) {
                errorMessageState.value = e.message
            } finally {
                isLoadingState.value = false
            }
        }
    }
}

@Composable
fun useUser(token: String, userId: Int): UserViewModel {
    val viewModel = remember { UserViewModel(token) }

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    return viewModel
}
