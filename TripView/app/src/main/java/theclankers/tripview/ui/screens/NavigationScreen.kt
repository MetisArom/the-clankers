package theclankers.tripview.ui.screens

import android.R.attr.text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.model.cameraPosition
import theclankers.tripview.ui.navigation.navigateTo

@Composable
fun NavigationScreen(navController: NavHostController) { Button(onClick = {
    navigateTo(navController, "camera2")
}) { Text("Go to Camera 2 Screen")  }

    val cameraPositionState = rememberCameraPositionState()
    var mapLoaded by remember { mutableStateOf(false) }

    val stops = listOf(
        LatLng(42.2776, -83.7409), // Gallup
        LatLng(42.2456, -83.7106), // Cobblestone Farm
        LatLng(42.2656, -83.7487),  // Michigan Stadium
        LatLng(42.2804, -83.7495)  // Frita Batidos
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(mapType = MapType.NORMAL, isMyLocationEnabled = false),
        uiSettings = MapUiSettings(compassEnabled = true, mapToolbarEnabled = false),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { mapLoaded = true }
    ) {
        // Add markers for each waypoint
        stops.forEachIndexed { index, latLng ->
            Marker(
                state = MarkerState(position = latLng),
                title = "Waypoint ${index + 1}",
                snippet = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
            )
        }

        Polyline(
            points = stops,
            color = androidx.compose.ui.graphics.Color.Blue,
            width = 8f
        )

        LaunchedEffect(mapLoaded, stops) {
            if (mapLoaded && stops.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.builder()
                stops.forEach { boundsBuilder.include(it) }
                val bounds = boundsBuilder.build()

                // Use padding for spacing (in pixels)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngBounds(bounds, 150)
                )
            }
        }
    }
}