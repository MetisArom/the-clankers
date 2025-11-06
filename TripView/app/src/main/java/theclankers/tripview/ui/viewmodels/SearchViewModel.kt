package theclankers.tripview.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import theclankers.tripview.data.api.ApiClient

class SearchViewModel(private val token: String) : ViewModel() {
    // Expose individual states
    val searchResultsState = mutableStateOf<List<Int>?>(null)

    val isLoadingState = mutableStateOf(false)
    val errorMessageState = mutableStateOf<String?>(null)

    fun search(query: String) {
        viewModelScope.launch {
            isLoadingState.value = true
            errorMessageState.value = null
            try {
                val searchResults = ApiClient.searchFriends(token, query)
                searchResultsState.value = searchResults
            } catch (e: Exception) {
                errorMessageState.value = e.message
            } finally {
                isLoadingState.value = false
            }
        }
    }
}

@Composable
fun useSearch(token: String): SearchViewModel {
    val viewModel = remember { SearchViewModel(token) }
    return viewModel
}
