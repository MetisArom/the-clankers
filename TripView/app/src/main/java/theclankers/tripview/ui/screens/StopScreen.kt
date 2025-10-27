package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import theclankers.tripview.classes.Stop
import theclankers.tripview.ui.navigation.goBack

@Composable
fun StopScreen(navController: NavHostController, stop: Stop) {
    Column {
        Text("This is the stop page for stop $stop.id")
        Button(onClick = {
            goBack(navController)
        }) { Text("Go back") }
    }
}