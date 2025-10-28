package theclankers.tripview.ui.navigation



import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import theclankers.tripview.ui.components.ProfileComponent

@Composable
fun CameraScreen(navController: NavHostController) { Button(onClick = {
    navigateTo(navController, "camera2")
}) { Text("Go to Camera 2 Screen")  } }
@Composable
fun FriendsScreen() { Text("Friends Screen") }
@Composable
fun ProfileScreen() {
        ProfileComponent(1)
}

@Composable
fun Camera2Screen() { Text("Camera 2 Screen") }