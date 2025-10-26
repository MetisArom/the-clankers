package theclankers.tripview.ui.navigation

import android.R.attr.text
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) { Button(onClick = {
    navigateTo(navController, "navigation")
}) { Text("Go to Navigation Screen")  } }
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