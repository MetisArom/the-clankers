package theclankers.tripview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theclankers.tripview.ui.components.BackButton
import theclankers.tripview.ui.components.FriendProfileComponent
import theclankers.tripview.ui.components.TripItem

@Composable
fun FriendProfileScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF7F6F8))
        .padding(16.dp)
        .verticalScroll(rememberScrollState())){


        BackButton(navController)
        //example user
        FriendProfileComponent(1, navController)

        // Example Trip list
        TripItem(
            tripName = "Trip 1",
            tripDescription = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
            onClick = {
                navController.navigate("tripdetail/1")
                //will update this as i make more screens }
            }
            //modifier = Modifier.padding(bottom=24.dp)
        )
        TripItem(
            tripName = "Trip 2",
            tripDescription = "Supporting line text lorem ipsum dolor sit amet, consectetur.",
            onClick = {
                navController.navigate("tripdetail/2")
                //will update this as i make more screens }
            }
        )
    }

}