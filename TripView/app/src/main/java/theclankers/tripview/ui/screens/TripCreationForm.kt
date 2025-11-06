package theclankers.tripview.ui.screens

import androidx.compose.foundation.background
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
import theclankers.tripview.data.api.ApiClient.sendTripForm
import theclankers.tripview.ui.components.FormInput
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.viewmodels.SendFormViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useSendForm
import theclankers.tripview.ui.viewmodels.useUser

@Composable
fun TripCreationForm(navController: NavController) {
    var destination by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var stops by remember { mutableStateOf("") }
    var timeline by remember { mutableStateOf("") }

    val appVM = useAppContext()
    val userID = appVM.userIdState.value
    val token = appVM.accessTokenState.value

    if(userID == null || token == null) return

    val sendFormVM = useSendForm(token)

    val isLoading by remember { derivedStateOf { sendFormVM.isLoadingState.value } }
    val trips = sendFormVM.tripSuggestions
    val error by remember { derivedStateOf { sendFormVM.errorMessageState.value } }

    LaunchedEffect(isLoading, trips.size) {
        // only navigate when loading finished and there are trips (successful result)
        if (!isLoading && trips.isNotEmpty()) {
            navigateToDetail(navController, "TripFormPt2")
        }
    }

    fun onSubmit() {
        // just start the request; navigation happens via LaunchedEffect above
        sendFormVM.sendForm(destination, days, stops, timeline, "1")
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
                    value = destination,
                    onValueChange = { destination = it },
                    label = "Where are you going?",
                    placeholder = "e.g., Paris, France",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = days,
                    onValueChange = { days = it },
                    label = "How many days are you going for?",
                    placeholder = "e.g., 5",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = stops,
                    onValueChange = { stops = it },
                    label = "Where are you staying along the way?",
                    placeholder = "e.g., Hotel Name, City",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = timeline,
                    onValueChange = { timeline = it },
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
                    onClick = { onSubmit() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    if (isLoading) {
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
