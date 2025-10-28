package theclankers.tripview.ui.navigation



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import theclankers.tripview.ui.components.ProfilePageComponent
import theclankers.tripview.ui.components.TripItem

@Composable
fun CameraScreen(navController: NavHostController) { Button(onClick = {
    navigateTo(navController, "camera2")
}) { Text("Go to Camera 2 Screen")  } }
@Composable
fun FriendsScreen() { Text("Friends Screen") }
@Composable
fun ProfileScreen(navController: NavController) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6F8))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())){

            //example user
            ProfilePageComponent(1, navController)



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