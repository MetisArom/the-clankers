package theclankers.tripview.ui.screens

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import theclankers.tripview.data.models.Stop
import theclankers.tripview.ui.components.HeaderText
import theclankers.tripview.ui.navigation.goBack
import theclankers.tripview.ui.navigation.navigateTo
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useTrip
import theclankers.tripview.utils.decodePolyline

@Composable
fun NavigationScreen(navController: NavHostController) {
    //returns 0 if no argument, bounce back
    val tripId = navController.currentBackStackEntry?.arguments?.getInt("tripId") ?: 0
    if (tripId == 0) {
        goBack(navController)
        return
    }

    val activityVM: AppViewModel = viewModel(LocalActivity.current as ComponentActivity)

    val tripState = useTrip(activityVM.authAccessToken.value, tripId)
    val trip = tripState.value

    val cameraPositionState = rememberCameraPositionState()
    var mapLoaded by remember { mutableStateOf(false) }

    var showDirectPolyline by remember { mutableStateOf(false) }
    var showDrivingPolyline by remember { mutableStateOf(false) }

    val drivingPoints by produceState(initialValue = emptyList(), trip?.drivingPolyline) {
        withContext(Dispatchers.Default) {
            value = decodePolyline(trip?.drivingPolyline ?: "")
        }
    }

    val stops = trip?.stops

    Row(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HeaderText("Map Loading...")
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(mapType = MapType.NORMAL, isMyLocationEnabled = false),
        uiSettings = MapUiSettings(compassEnabled = true, mapToolbarEnabled = false),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { mapLoaded = true } // Run Launched Effect at this point
    ) {
        // Add markers for each waypoint
        stops?.forEachIndexed { index, stop ->
            Marker(
                state = MarkerState(position = LatLng(stop.latitude, stop.longitude)),
                title = "Waypoint ${index + 1}",
                snippet = "Lat: ${stop.latitude}, Lng: ${stop.longitude}, stop id: ${stop.stopId}",
                onClick = {
                    val stopJson = Uri.encode(Json.encodeToString(stop))
                    navigateTo(navController, "stops/$stopJson")
                    true
                }
            )
        }

        //Direct Route Polyline
        if (showDirectPolyline) {
            Polyline(
                points = stops?.map { LatLng(it.latitude, it.longitude) } ?: emptyList(),
                color = Color(0xFF0F53FF), // Google Maps Blue
                width = 16f,                   // Thicker line
                jointType = JointType.ROUND,   // Rounded joins
                startCap = RoundCap(),
                endCap = RoundCap()
            )
        }

        //Driving Route Polyline
        if (showDrivingPolyline) {
            Polyline(
                points = drivingPoints,
                color = Color(0xFF0F53FF), // Google Maps Blue
                width = 16f,                   // Thicker line
                jointType = JointType.ROUND,   // Rounded joins
                startCap = RoundCap(),
                endCap = RoundCap()
            )
        }

        // Zoom map view into stops bounding box
        LaunchedEffect(mapLoaded, stops) {
            if (mapLoaded && stops != null && stops.isNotEmpty()) {
                //Compute bounding box
                val boundsBuilder = LatLngBounds.builder()
                stops.forEach { boundsBuilder.include(LatLng(it.latitude, it.longitude)) }
                val bounds = boundsBuilder.build()

                // Zoom to bounding box, use padding for spacing (in pixels)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngBounds(bounds, 150)
                )
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(0.88F).padding(bottom = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    )
    {
        Button(
            onClick = {
                showDirectPolyline = !showDirectPolyline
                if (showDrivingPolyline) {
                    showDrivingPolyline = false
                }
            }
        ) {
            if (!showDirectPolyline) {
                Text("Show Direct Route")
            } else {
                Text("Hide Direct Route")
            }
        }
        Button(
            onClick = {
                showDrivingPolyline = !showDrivingPolyline
                if (showDirectPolyline) {
                    showDirectPolyline = false
                }
            },
            enabled = drivingPoints.isNotEmpty() //Don't let user click if driving route not decoded yet
        ) {
            if (!showDrivingPolyline) {
                Text("Show Driving Route")
            } else {
                Text("Hide Driving Route")
            }
        }
    }
}