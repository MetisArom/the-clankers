package theclankers.tripview.ui.navigation

import android.R.attr.onClick
import androidx.compose.foundation.background
import android.R.id.bold
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import theclankers.tripview.R
import theclankers.tripview.ui.components.HeaderText
import theclankers.tripview.ui.components.HeaderText1

import theclankers.tripview.ui.components.ProfilePageComponent
import theclankers.tripview.ui.components.TripItem
import theclankers.tripview.ui.viewmodels.getAuthedUser


@Composable
fun CameraScreen(navController: NavHostController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF7F6F8))
        .padding(24.dp)
        .verticalScroll(rememberScrollState())){
        HeaderText1("Camera",  modifier = Modifier.padding(bottom=16.dp))
        Text(text="Take a photo of a landmark to get more info", fontWeight= FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { navigateTo(navController, "camera2") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D))
        ){
            Icon(
                painter = painterResource(R.drawable.camera_icon),
                contentDescription = "camera"
            )
        }
    }

}
@Composable
fun FriendsScreen() { Text("Friends Screen") }
@Composable
fun ProfileScreen(navController: NavController) {
        val authedUserId = getAuthedUser().value?.userId ?: 0

        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F8))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())){

            //example user
            ProfilePageComponent(authedUserId, navController)



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

@Composable
fun Camera2Screen() { Text("Camera 2 Screen") }

@Composable
fun StopScreen(navController: NavHostController, itemId: Int?) {
    Column {
        Text("This is the stop page for stop $itemId")
        Button(onClick = {
            goBack(navController)
        }) { Text("Go back") }
    }
}