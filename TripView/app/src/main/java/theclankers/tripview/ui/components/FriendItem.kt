package theclankers.tripview.ui.components

import android.R.attr.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close

// Abstract container component for lists of people, trips, stops, etc.


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
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
fun FriendItem(otherUserId: Int) {
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

    // TODO: Implement the UI for each of the 5 below cases
    // relationship will be one of "friends", "pending_incoming", "pending_outgoing", "none", "self"

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
