package theclankers.tripview.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.JointType
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
import theclankers.tripview.ui.components.HeaderText
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.StopViewModel
import theclankers.tripview.ui.viewmodels.TripViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useStop
import theclankers.tripview.ui.viewmodels.useTrip
import theclankers.tripview.utils.decodePolyline

@Composable
fun NavigationScreen(navController: NavHostController, tripId: Int) {
    val tripVM: TripViewModel = useTrip("token", tripId)
    val drivingPolyline = tripVM.drivingPolylineState.value ?: ""

    val cameraPositionState = rememberCameraPositionState()
    var mapLoaded by remember { mutableStateOf(false) }

    var showDirectPolyline by remember { mutableStateOf(false) }
    var showDrivingPolyline by remember { mutableStateOf(false) }

    // Get LatLng pairs of all stops on route for direct polyline
    val directPoints = tripVM.stops.value.map {
        LatLng(it.latitude, it.longitude)
    }

    // Decode encoded polyline string to LatLng pairs for driving polyline. Offload to separate thread.
    val drivingPoints by produceState(initialValue = emptyList(), drivingPolyline) {
        withContext(Dispatchers.Default) {
            value = decodePolyline(drivingPolyline)
        }
    }

    // Placeholder loading text for when map is not done loading
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
        onMapLoaded = { mapLoaded = true }
    ) {
        tripVM.stops.value.forEachIndexed { index, stop ->
            Marker(
                state = MarkerState(position = LatLng(stop.latitude, stop.longitude)),
                title = "Waypoint ${index + 1}",
                // Navigate to detail page for the stop you click on
                onClick = {
                    navigateToDetail(navController, "stop/${stop.stopId}")
                    true
                }
            )
        }

        if (showDirectPolyline) {
            Polyline(
                points = directPoints,
                color = Color(0xFF0F53FF),
                width = 16f,
                jointType = JointType.ROUND,
                startCap = RoundCap(),
                endCap = RoundCap()
            )
        }

        if (showDrivingPolyline) {
            Polyline(
                points = drivingPoints,
                color = Color(0xFF0F53FF),
                width = 16f,
                jointType = JointType.ROUND,
                startCap = RoundCap(),
                endCap = RoundCap()
            )
        }

        LaunchedEffect(mapLoaded, directPoints) {
            if (mapLoaded && directPoints.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.builder()
                directPoints.forEach { boundsBuilder.include(it) }
                val bounds = boundsBuilder.build()
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 150))
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(0.88f).padding(bottom = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                showDirectPolyline = !showDirectPolyline
                if (showDrivingPolyline) showDrivingPolyline = false
            },
            enabled = directPoints.isNotEmpty()
        ) {
            Text(if (!showDirectPolyline) "Show Direct Route" else "Hide Direct Route")
        }

        Button(
            onClick = {
                showDrivingPolyline = !showDrivingPolyline
                if (showDirectPolyline) showDirectPolyline = false
            },
            enabled = drivingPoints.isNotEmpty()
        ) {
            Text(if (!showDrivingPolyline) "Show Driving Route" else "Hide Driving Route")
        }
    }
}
