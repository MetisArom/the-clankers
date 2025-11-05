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

class FriendsViewModel(private val token: String) : ViewModel() {
    val friendsState: MutableState<List<Int>?> = mutableStateOf(null)
    val isLoadingState: MutableState<Boolean> = mutableStateOf(false)
    val errorMessageState: MutableState<String?> = mutableStateOf(null)

    fun getFriends(userId: Int) {
        viewModelScope.launch {
            isLoadingState.value = true
            errorMessageState.value = null
            try {
                val friends = withContext(Dispatchers.IO) {
                    ApiClient.getFriends(token, userId)
                }
                friendsState.value = friends
            } catch (e: Exception) {
                errorMessageState.value = "Error loading friends: ${e.message}"
            } finally {
                isLoadingState.value = false
            }
        }
    }
}

@Composable
fun useFriends(token: String, userId: Int): FriendsViewModel {
    val viewModel = remember { FriendsViewModel(token) }

    LaunchedEffect(userId) {
        viewModel.getFriends(userId)
    }

    return viewModel
}