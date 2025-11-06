@file:JvmName("UserItemKt")

package theclankers.tripview.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theclankers.tripview.ui.theme.Purple1
import theclankers.tripview.ui.theme.Purple4
import theclankers.tripview.ui.theme.Purple80
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useRelationship
import theclankers.tripview.ui.viewmodels.useUser

@Composable
fun UserItem(otherUserId: Int) {
    val appVM: AppViewModel = useAppContext()
    val userId = appVM.userIdState.value
    val token = appVM.accessTokenState.value

    if (userId == null || token == null) return

    val otherUserVM = useUser(token, otherUserId)
    val firstName = otherUserVM.firstNameState.value
    val lastName = otherUserVM.lastNameState.value
    val username = otherUserVM.usernameState.value

    if (firstName == null || lastName == null || username == null) return

    val relationshipVM = useRelationship(token, userId, otherUserId)
    val relationship = relationshipVM.relationshipState.value

    // Wait for the relationship to be gotten
    if (relationship == null) return

    val displayName = "$firstName $lastName"
    // relationship will be one of "friends", "pending_incoming", "pending_outgoing", "none", "self"

    // Define wrapper functions for adjusting relationship when each of the buttons are clicked
    fun interact(action: String) {
        if (action == "invite") {
            relationshipVM.relationshipState.value = "pending_outgoing"
            otherUserVM.invite(otherUserId)
        } else if (action == "accept") {
            relationshipVM.relationshipState.value = "friends"
            otherUserVM.accept(otherUserId)
        } else if (action == "decline") {
            relationshipVM.relationshipState.value = "none"
            otherUserVM.decline(otherUserId)
        } else if (action == "revoke") {
            relationshipVM.relationshipState.value = "none"
            otherUserVM.revoke(otherUserId)
        } else if (action == "remove") {
            relationshipVM.relationshipState.value = "none"
            otherUserVM.remove(otherUserId)
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Purple4,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Avatar + Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Purple80,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = username.firstOrNull()?.uppercase() ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "@$username",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }

            // Right: Relationship actions
            when (relationship) {
                "friends" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Friends",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { interact("remove") },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple1)
                        ) {
                            Text("Remove")
                        }
                    }
                }

                "pending_incoming" -> {
                    Row {
                        IconButton(
                            onClick = { interact("accept") },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Purple1, RoundedCornerShape(24.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Accept",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { interact("decline") },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Purple1, RoundedCornerShape(24.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Decline",
                                tint = Color.White
                            )
                        }
                    }
                }

                "pending_outgoing" -> {
                    Button(
                        onClick = { interact("revoke") },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple1)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Revoke"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Revoke Request")
                    }
                }

                "none" -> {
                    Button(
                        onClick = { interact("invite") },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple1)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Friend"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Friend")
                    }
                }

                "self" -> {
                    Text(
                        text = "You",
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }

    // I commented the old code to stop compile errors, but it just needs some adjustment!

//    Surface(
//        shape = RoundedCornerShape(12.dp),
//       // color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
//        color = Purple4,
//        modifier = modifier
//            .fillMaxWidth()
//            .clickable(enabled = onClick != null) { onClick?.invoke() },
//        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(12.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            // Left section: avatar + name
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Surface(
//                    shape = CircleShape,
//                    color = Purple80,
//                    modifier = Modifier.size(40.dp)
//                ) {
//                    Box(contentAlignment = Alignment.Center) {
//                        Text(
//                            text = username.firstOrNull()?.uppercase() ?: "",
//                            style = MaterialTheme.typography.bodyMedium.copy(
//                                fontWeight = FontWeight.Bold,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.width(12.dp))
//
//                Column {
//                    HelperText2(
//                        text = displayName,
//                        //fontWeight = FontWeight.SemiBold,
//                        //fontSize = 16.sp
//                    )
//                    Text(
//                        text = username,
//                        color = Color.Gray,
//                        fontSize = 13.sp
//                    )
//                }
//            }
//
//            // Right section: optional accept/decline buttons
////            if (showActions) {
////                Row {
////                    IconButton(onClick = { onAccept?.invoke() }) {
////                        Icon(
////                            imageVector = Icons.Default.Check,
////                            contentDescription = "Accept",
////                            tint = MaterialTheme.colorScheme.primary
////                        )
////                    }
////                    IconButton(onClick = { onDecline?.invoke() }) {
////                        Icon(
////                            imageVector = Icons.Default.Close,
////                            contentDescription = "Decline",
////                            tint = Color.Gray
////                        )
////                    }
////                }
////            }
//            if (showActions) {
//                Row {
//                    IconButton(
//                        onClick = { onAccept?.invoke() },
//                        modifier = Modifier
//                            .size(48.dp) // Adjust size as needed
//                            .background(
//                                //color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
//                                color = Purple1,
//                                shape = RoundedCornerShape(24.dp) // Makes it oval
//                            )
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = "Accept",
//                           // tint = MaterialTheme.colorScheme.primary
//                            tint = Color.White
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp)) // space between buttons
//
//                    IconButton(
//                        onClick = { onDecline?.invoke() },
//                        modifier = Modifier
//                            .size(48.dp)
//                            .background(
//                                //color = Color.Gray.copy(alpha = 0.1f),
//                                color = Purple1,
//                                shape = RoundedCornerShape(24.dp)
//                            )
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Close,
//                            contentDescription = "Decline",
//                            tint = Color.White
//                        )
//                    }
//                }
//            }
//
//        }
//    }
}
