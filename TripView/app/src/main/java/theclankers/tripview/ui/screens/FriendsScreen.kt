package theclankers.tripview.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.ListComponent
import theclankers.tripview.ui.components.TitleText
import theclankers.tripview.ui.components.UserItem
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.FriendsViewModel
import theclankers.tripview.ui.viewmodels.InvitesViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useFriends
import theclankers.tripview.ui.viewmodels.useInvites

@Composable
fun FriendsScreen(navController: NavController) {
    val appVM: AppViewModel = useAppContext()
    val userId = appVM.userIdState.value
    val token = appVM.accessTokenState.value

    if (userId == null || token == null) return

    val friendsVM: FriendsViewModel = useFriends(token, userId)
    val friends = friendsVM.friendsState.value

    Log.d("FriendsScreen", "Friends: $friends")

    val invitesVM: InvitesViewModel = useInvites(token, userId)
    val invites = invitesVM.invitesState.value

    Log.d("FriendsScreen", "Invites: $invites")

    // TODO: Make sure the below line makes sense... Might not need it?
    if (friends == null || invites == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleText("Your Friends")

        ListComponent(friends) { friendId ->
            UserItem(friendId)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Invite Requests", style = MaterialTheme.typography.titleLarge)

        ListComponent(invites) { inviteId ->
            UserItem(inviteId)
        }
    }
}
