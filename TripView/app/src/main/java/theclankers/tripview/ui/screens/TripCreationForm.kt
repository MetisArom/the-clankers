package theclankers.tripview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import theclankers.tripview.ui.navigation.TripViewNavigationBar

@Composable
fun TripCreationForm(navController: NavController) {
    var destination by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var stops by remember { mutableStateOf("") }
    var timeline by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFF7F6F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
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

                TripInputField(
                    label = "Where are you going?",
                    value = destination,
                    onValueChange = { destination = it }
                )

                TripInputField(
                    label = "How many days are you going for?",
                    value = days,
                    onValueChange = { days = it }
                )

                TripInputField(
                    label = "Where are you staying along the way?",
                    value = stops,
                    onValueChange = { stops = it }
                )

                TripInputField(
                    label = "Please specify the timeline of your trip, dates where you have to be in certain places",
                    value = timeline,
                    onValueChange = { timeline = it }
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { navController.navigate("tripcreationform1") },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit", color = Color.White) //will lead to Trip Creation Form - Part 2
                }
            }
        }
    }
}

@Composable
private fun TripInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),
                cursorColor = Color.Black
            )
        )
    }
}
