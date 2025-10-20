package theclankers.tripview.ui.navigation

import android.R.attr.text
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen() { Text("Home Screen") }
@Composable
fun CameraScreen() { Text("Camera Screen") Button(text="Go to Camera2 Screen") }
@Composable
fun FriendsScreen() { Text("Friends Screen") }
@Composable
fun ProfileScreen() { Text("Profile Screen") }

@Composable
fun Camera2Screen() { Text("Camera Screen") }