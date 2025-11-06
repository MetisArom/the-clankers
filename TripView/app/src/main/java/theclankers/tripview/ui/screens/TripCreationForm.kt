package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.FormInput
import theclankers.tripview.ui.viewmodels.useAppContext

@Composable
fun TripCreationForm(navController: NavController) {
    val appVM = useAppContext()
    val userID = appVM.userIdState.value
    val token = appVM.accessTokenState.value

    if(userID == null || token == null) return

    if (appVM.isLoadingState.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        containerColor = Color(0xFFF7F6F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Trip Creation Form",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create New Trip",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(24.dp))

                FormInput(
                    value = appVM.destination.value,
                    onValueChange = { appVM.destination.value = it },
                    label = "Where are you going?",
                    placeholder = "e.g., Paris, France",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = appVM.numDays.value,
                    onValueChange = { appVM.numDays.value = it },
                    label = "How many days are you going for?",
                    placeholder = "e.g., 5",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = appVM.stops.value,
                    onValueChange = { appVM.stops.value = it },
                    label = "Where are you staying along the way?",
                    placeholder = "e.g., Hotel Name, City",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = appVM.timeline.value,
                    onValueChange = { appVM.timeline.value = it },
                    label = "Please specify the timeline of your trip, dates where you have to be in certain places",
                    placeholder = "e.g., Day 1: Paris, Day 3: Lyon",
                    maxLines = 3,
                    singleLine = false,
                    imeAction = ImeAction.Done,
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = { appVM.submitForm(navController) },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    if (appVM.isLoadingState.value) {
                       Text("Submitting...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Submit",
                            color = Color.White
                        ) //will lead to Trip Creation Form - Part 2
                    }
                }
            }
        }
    }
}
