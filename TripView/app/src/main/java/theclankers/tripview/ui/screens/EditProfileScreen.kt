package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.FormInput
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useUser

@Composable
fun EditProfileScreen(navController: NavController) {
    val appVM = useAppContext()
    val userId = appVM.userIdState.value
    val token = appVM.accessTokenState.value

    if (userId == null || token == null) return

    val userVM = useUser(token, userId)
    val initialFirstName = userVM.firstNameState.value
    val initialLastName = userVM.lastNameState.value
    val initialUsername = userVM.usernameState.value
    // TODO: Get initial variables for likes and dislikes

    if (initialFirstName == null || initialLastName == null || initialUsername == null) return

    // Local states stored as MutableState
    val firstName = remember { mutableStateOf(initialFirstName) }
    val lastName = remember { mutableStateOf(initialLastName) }
    val username = remember { mutableStateOf(initialUsername) }
    // TODO: Create state for likes and dislikes
    // TODO: Create state for loading

    // TODO: Implement onSubmit, disables all buttons on the screen using a new loading state, then calls userVM.editUser(...), then navigates to ProfileScreen
    fun onSubmit() {

    }

    Scaffold(containerColor = Color(0xFFF7F6F8)) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = "Edit Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (userVM.isLoadingState.value) {
                    Text("Loading user info...", color = Color.Gray)
                } else if (userVM.errorMessageState.value != null) {
                    Text("Error: ${userVM.errorMessageState.value}", color = Color.Red)
                } else {
                    FormInput(
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = "Username:",
                        placeholder = "JaneDoe123",
                        imeAction = ImeAction.Done,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    FormInput(
                        value = firstName.value,
                        onValueChange = { firstName.value = it },
                        label = "First Name:",
                        placeholder = "Jane",
                        imeAction = ImeAction.Next,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    FormInput(
                        value = lastName.value,
                        onValueChange = { lastName.value = it },
                        label = "Last Name:",
                        placeholder = "Doe",
                        imeAction = ImeAction.Next,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // TODO: Create form inputs for likes and dislikes
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    // TODO: Make this button disabled when loading
                    Button(
                        onClick = { onSubmit() },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Changes", color = Color.White)
                    }
                }
            }
        }
    }
}
