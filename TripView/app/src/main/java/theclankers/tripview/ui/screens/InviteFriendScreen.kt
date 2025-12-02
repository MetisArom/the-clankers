package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.ListComponent
import theclankers.tripview.ui.components.SearchBar
import theclankers.tripview.ui.components.UserItem
import theclankers.tripview.ui.viewmodels.useAppContext
import androidx.compose.material3.Button
import theclankers.tripview.ui.components.UserInviteRow
import theclankers.tripview.ui.viewmodels.useSearch
import theclankers.tripview.ui.viewmodels.useTrip
import theclankers.tripview.ui.viewmodels.useUser

@Composable
fun InviteFriendScreen(navController: NavController, tripId: Int) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value
    val userId = appVM.userIdState.value

    if (token == null || userId == null) return

    val searchVM = useSearch(token)
    val tripVM = useTrip(token, tripId)

    val searchResults = searchVM.searchResultsState.value
    val invitedFriends by tripVM.invitedFriendsState

    // 🔥 Per-row loading states
    val rowLoading = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Invite Friends",
            modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
        )

        SearchBar(
            onQuery = { query -> searchVM.search(query) },
            prefill = "",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔍 Search results list
        searchResults?.forEach { friendId ->

            val alreadyInvited = invitedFriends.contains(friendId)
            val isLoading = rowLoading[friendId] == true

            UserInviteRow(
                token = token,
                friendId = friendId,
                alreadyInvited = alreadyInvited,
                isLoading = isLoading,
                onInvite = {
                    rowLoading[friendId] = true
                    tripVM.inviteFriend(tripId, friendId) {
                        rowLoading[friendId] = false
                    }
                },
                onUninvite = {
                    rowLoading[friendId] = true
                    tripVM.uninviteFriend(tripId, friendId) {
                        rowLoading[friendId] = false
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}