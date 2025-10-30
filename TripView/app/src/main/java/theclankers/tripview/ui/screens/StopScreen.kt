package theclankers.tripview.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import theclankers.tripview.data.models.Stop
import theclankers.tripview.ui.navigation.goBack
import androidx.core.net.toUri

@Composable
fun StopScreen(navController: NavHostController, stop: Stop) {
    val context = LocalContext.current

    Column {
        Text("This is the stop page for stop $stop.id")
        Button(onClick = {
            goBack(navController)
        }) { Text("Go back") }
        Button(onClick = {
            //Build the URI
            val uri =
                "https://www.google.com/maps/dir/?api=1&destination=${stop.latitude},${stop.longitude}&travelmode=driving".toUri()

            //Create intent to open the Google Maps app explicitly if it exists
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                //Explicitly open Google Maps
                setPackage("com.google.android.apps.maps")
            }

            // Try to start the activity
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback: open in browser if Google Maps app is not installed
                val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(browserIntent)
            }
        }) { Text("Get Directions") }
    }
}