package theclankers.tripview.ui.components

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

@Composable
fun FriendItem(
    //for list of people
    username: String,
    displayName: String,
    modifier: Modifier = Modifier,
    showActions: Boolean = false,        // e.g. for invite requests
    onAccept: (() -> Unit)? = null,
    onDecline: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
       // color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        color = Purple4,
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left section: avatar + name
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
                    HelperText2(
                        text = displayName,
                        //fontWeight = FontWeight.SemiBold,
                        //fontSize = 16.sp
                    )
                    Text(
                        text = username,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }

            // Right section: optional accept/decline buttons
//            if (showActions) {
//                Row {
//                    IconButton(onClick = { onAccept?.invoke() }) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = "Accept",
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                    IconButton(onClick = { onDecline?.invoke() }) {
//                        Icon(
//                            imageVector = Icons.Default.Close,
//                            contentDescription = "Decline",
//                            tint = Color.Gray
//                        )
//                    }
//                }
//            }
            if (showActions) {
                Row {
                    IconButton(
                        onClick = { onAccept?.invoke() },
                        modifier = Modifier
                            .size(48.dp) // Adjust size as needed
                            .background(
                                //color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                color = Purple1,
                                shape = RoundedCornerShape(24.dp) // Makes it oval
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Accept",
                           // tint = MaterialTheme.colorScheme.primary
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp)) // space between buttons

                    IconButton(
                        onClick = { onDecline?.invoke() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                //color = Color.Gray.copy(alpha = 0.1f),
                                color = Purple1,
                                shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Decline",
                            tint = Color.White
                        )
                    }
                }
            }

        }
    }
}
