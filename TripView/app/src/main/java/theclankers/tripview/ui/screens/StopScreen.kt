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
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useStop
import theclankers.tripview.ui.viewmodels.useStopById

@Composable
fun StopScreen(navController: NavHostController, stopId: Int) {
    val context = LocalContext.current

    val appVM = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val stopVM = useStopById(token, stopId)
    val latitude = stopVM.latitudeState.value
    val longitude = stopVM.longitudeState.value

    Column {
        Text("This is the stop page for stop $stopId")
        Button(onClick = {
            goBack(navController)
        }) { Text("Go back") }
        Button(onClick = {
            //Build the URI
            val uri =
                "https://www.google.com/maps/dir/?api=1&destination=${latitude},${longitude}&travelmode=driving".toUri()

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