package theclankers.tripview.ui.screens

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
import theclankers.tripview.ui.components.FriendItem
import theclankers.tripview.ui.components.HeaderText1

@Composable
fun FriendsListScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderText1("Your Friends")

        FriendItem(username = "janesmith67", displayName = "Jane Smith", onClick = { navController.navigate("friendProfile") })
        FriendItem(username = "ozzy67", displayName = "Ozzy Osbourne", onClick = { navController.navigate("friendProfile")})

        Spacer(modifier = Modifier.height(16.dp))
        Text("Invite Requests", style = MaterialTheme.typography.titleLarge)

        FriendItem(
            username = "andrew45",
            displayName = "Andrew",
            showActions = true,
            onAccept = { /* handle accept */ },
            onDecline = { /* handle decline */ },
            onClick = { navController.navigate("friendProfile")}
        )
    }
}
