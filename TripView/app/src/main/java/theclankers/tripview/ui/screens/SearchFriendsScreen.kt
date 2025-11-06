package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.ListComponent
import theclankers.tripview.ui.components.SearchBar
import theclankers.tripview.ui.components.UserItem
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useSearch

@Composable
fun SearchFriendsScreen(navController: NavController) {
    val appVM: AppViewModel = useAppContext()
    val userId = appVM.userIdState.value
    val token = appVM.accessTokenState.value

    if (userId == null || token == null) return

    val searchVM = useSearch(token)
    val searchResults = searchVM.searchResultsState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🟣 Use your custom SearchBar component
        SearchBar(
            onQuery = { query ->
                searchVM.search(query)
            },
            prefill = "",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔍 Placeholder for search results
        // You can replace this with a LazyColumn when ready
        if (searchResults != null) {
            ListComponent(searchResults) { resultId ->
                UserItem(resultId)
            }
        }
    }
}
