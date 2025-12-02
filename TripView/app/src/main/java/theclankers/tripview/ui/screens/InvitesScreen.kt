package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.UserInviteRow
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useTrip

@Composable
fun InvitesScreen(navController: NavController, tripId: Int) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value
    val userId = appVM.userIdState.value

    if (token == null || userId == null) return

    val tripVM = useTrip(token, tripId)
    val invitedFriends by tripVM.invitedFriendsState

    // 🔥 Per-row loading states
    val rowLoading = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // ⏺ Top Button: Invite Friends
        Button(
            onClick = { navController.navigate("inviteFriend/$tripId") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Invite Friends", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Invited Friends",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        // 🧑‍🤝‍🧑 Show list of invited friends
        if (invitedFriends.isEmpty()) {
            Text(
                text = "No invitations sent yet.",
                modifier = Modifier.padding(8.dp)
            )
        } else {
            invitedFriends.forEach { friendId ->
                val isLoading = rowLoading[friendId] == true

                UserInviteRow(
                    token = token,
                    friendId = friendId,
                    alreadyInvited = true, // all friends here are invited
                    isLoading = isLoading,
                    onInvite = {}, // no-op, should not happen here
                    onUninvite = {
                        rowLoading[friendId] = true
                        tripVM.uninviteFriend(tripId, friendId) {
                            rowLoading[friendId] = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
