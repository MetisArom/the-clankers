package theclankers.tripview.ui.navigation

import android.R.id.bold
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import theclankers.tripview.ui.theme.Purple80
import theclankers.tripview.ui.theme.PurpleGrey80


@Composable
fun HomeScreen(navController: NavHostController) {
    Column() {
        Button(onClick = {
            navigateTo(navController, "navigation")
        }) { Text("Go to Navigation Screen") }

        Text(text = "Your Trips", fontSize = 40.sp, color = Purple80)// need this darker
        //Add component/button for create a trip
        // add list component for user's trip
        Text(text = "Friends' Trips", fontSize = 40.sp, color = Purple80)// need this darker
        //add list component for friend trips
        Text(text = "Completed Trips", fontSize = 40.sp, color = Purple80)// need this darker
        // add list component for friend trips
    }
}
@Composable
fun CameraScreen(navController: NavHostController) { Button(onClick = {
    navigateTo(navController, "camera2")
}) { Text("Go to Camera 2 Screen")  } }
@Composable
fun FriendsScreen() { Text("Friends Screen") }
@Composable
fun ProfileScreen() { Text("Profile Screen") }

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