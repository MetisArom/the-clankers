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

// userId1 is the active user, userId2 is the other user
class RelationshipViewModel(private val token: String) : ViewModel() {
    val relationshipState: MutableState<String?> = mutableStateOf(null)
    val isLoadingState: MutableState<Boolean> = mutableStateOf(false)
    val errorMessageState: MutableState<String?> = mutableStateOf(null)

    fun getRelationship(userId1: Int, userId2: Int) {
        viewModelScope.launch {
            isLoadingState.value = true
            errorMessageState.value = null
            try {
                val relationship = withContext(Dispatchers.IO) {
                    ApiClient.getRelationship(token, userId1, userId2)
                }
                relationshipState.value = relationship
            } catch (e: Exception) {
                errorMessageState.value = "Error loading relationship: ${e.message}"
            } finally {
                isLoadingState.value = false
            }
        }
    }
}

@Composable
fun useRelationship(token: String, userId1: Int, userId2: Int): RelationshipViewModel {
    val viewModel = remember { RelationshipViewModel(token) }

    LaunchedEffect(userId1, userId2) {
        viewModel.getRelationship(userId1, userId2)
    }

    return viewModel
}