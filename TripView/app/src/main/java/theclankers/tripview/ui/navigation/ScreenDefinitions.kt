package theclankers.tripview.ui.navigation

import android.R.attr.text
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

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