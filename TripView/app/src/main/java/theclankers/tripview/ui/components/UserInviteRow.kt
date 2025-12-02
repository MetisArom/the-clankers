package theclankers.tripview.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import theclankers.tripview.ui.viewmodels.useUser

@Composable
fun UserInviteRow(
    token: String,
    friendId: Int,
    alreadyInvited: Boolean,
    isLoading: Boolean,
    onInvite: () -> Unit,
    onUninvite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // User info
        val userVM = useUser(token, friendId)
        val firstName = userVM.firstNameState.value
        val lastName = userVM.lastNameState.value
        val username = userVM.usernameState.value

        if (firstName != null && lastName != null && username != null) {
            Text(text = "$firstName $lastName (@$username)")

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Button(
                    onClick = if (alreadyInvited) onUninvite else onInvite
                ) {
                    Text(if (alreadyInvited) "Uninvite" else "Invite")
                }
            }
        }
    }
}
