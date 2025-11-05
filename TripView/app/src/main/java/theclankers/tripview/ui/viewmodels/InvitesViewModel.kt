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

class InvitesViewModel(private val token: String) : ViewModel() {
    val invitesState: MutableState<List<Int>?> = mutableStateOf(null)
    val isLoadingState: MutableState<Boolean> = mutableStateOf(false)
    val errorMessageState: MutableState<String?> = mutableStateOf(null)

    fun getInvites(userId: Int) {
        viewModelScope.launch {
            isLoadingState.value = true
            errorMessageState.value = null
            try {
                val invitesList = withContext(Dispatchers.IO) {
                    ApiClient.getInvites(token, userId)
                }
                invitesState.value = invitesList
            } catch (e: Exception) {
                errorMessageState.value = "Error loading invites: ${e.message}"
            } finally {
                isLoadingState.value = false
            }
        }
    }
}

@Composable
fun useInvites(token: String, userId: Int): InvitesViewModel {
    val viewModel = remember { InvitesViewModel(token) }

    LaunchedEffect(userId) {
        viewModel.getInvites(userId)
    }

    return viewModel
}