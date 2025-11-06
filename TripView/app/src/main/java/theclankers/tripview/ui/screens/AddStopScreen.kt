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
import theclankers.tripview.ui.viewmodels.useTrip
import theclankers.tripview.ui.viewmodels.useUser

@Composable
fun AddStopScreen(navController: NavController, tripId: Int) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val tripVM = useTrip(token, tripId)

    // Local states stored as MutableState
    val name = remember { mutableStateOf("") }
    val latitude = remember { mutableStateOf("") }
    val longitude = remember { mutableStateOf("") }

    // Disables all buttons on the edit profile screen using a new loading state,
    // then calls userVM.editUser(...)
    fun onSubmit() {
        tripVM.isAddingStopState.value = true
        tripVM.addStop(tripId, name.value, latitude.value, longitude.value)
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
                        text = "Ad",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                FormInput(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = "Name:",
                    placeholder = "",
                    imeAction = ImeAction.Done,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = latitude.value,
                    onValueChange = { latitude.value = it },
                    label = "Latitude:",
                    placeholder = "",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = longitude.value,
                    onValueChange = { longitude.value = it },
                    label = "Longitude:",
                    placeholder = "",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Button(
                        onClick = { onSubmit() },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        enabled = !tripVM.isAddingStopState.value
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

                Text(tripVM.addStopMessageState.value ?: "")
            }
        }
    }
}
