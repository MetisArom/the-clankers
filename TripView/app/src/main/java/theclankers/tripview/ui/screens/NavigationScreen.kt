package theclankers.tripview.ui.screens

import android.R.attr.onClick
import android.R.attr.text
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import kotlinx.serialization.json.Json
import theclankers.tripview.classes.Stop
import theclankers.tripview.ui.navigation.navigateTo

@Composable
fun NavigationScreen(navController: NavHostController) {
    val cameraPositionState = rememberCameraPositionState()
    var mapLoaded by remember { mutableStateOf(false) }

    val stops = listOf(
        Stop(1, 42.2776, -83.7409), // Gallup
        Stop(2, 42.2456, -83.7106), // Cobblestone Farm
        Stop(3, 42.2656, -83.7487),  // Michigan Stadium
        Stop(4, 42.2804, -83.7495)  // Frita Batidos
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(mapType = MapType.NORMAL, isMyLocationEnabled = false),
        uiSettings = MapUiSettings(compassEnabled = true, mapToolbarEnabled = false),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { mapLoaded = true }
    ) {
        // Add markers for each waypoint
        stops.forEachIndexed { index, stop ->
            Marker(
                state = MarkerState(position = LatLng(stop.latitude, stop.longitude)),
                title = "Waypoint ${index + 1}",
                snippet = "Lat: ${stop.latitude}, Lng: ${stop.longitude}, stop id: ${stop.id}",
                onClick = {
                    val stopJson = Uri.encode(Json.encodeToString(stop))
                    navigateTo(navController, "stops/$stopJson")
                    true
                }
            )
        }

        Polyline(
            points = stops.map { LatLng(it.latitude, it.longitude) },
            color = androidx.compose.ui.graphics.Color.Blue,
            width = 8f
        )

        LaunchedEffect(mapLoaded, stops) {
            if (mapLoaded && stops.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.builder()
                stops.forEach { boundsBuilder.include(LatLng(it.latitude, it.longitude)) }
                val bounds = boundsBuilder.build()

                // Use padding for spacing (in pixels)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngBounds(bounds, 150)
                )
            }
        }
    }
}