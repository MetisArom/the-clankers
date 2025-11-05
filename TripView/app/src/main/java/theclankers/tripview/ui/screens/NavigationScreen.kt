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
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.StopViewModel
import theclankers.tripview.ui.viewmodels.TripViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useStop
import theclankers.tripview.ui.viewmodels.useTrip
import theclankers.tripview.utils.decodePolyline

@Composable
fun NavigationScreen(navController: NavHostController, tripId: Int) {
    val appVM: AppViewModel = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val tripVM: TripViewModel = useTrip("token", tripId)
    val stopIds = tripVM.stopIdsState.value ?: emptyList()
    val drivingPolyline = tripVM.drivingPolylineState.value ?: ""

    // Create StopViewModels for each stopId
    val stopVMs: Map<Int, StopViewModel> = stopIds.associateWith { stopId ->
        useStop(token, stopId)
    }

    val stopPositions = stopVMs.values.mapNotNull { stopVM ->
        val lat = stopVM.latitudeState.value
        val lng = stopVM.longitudeState.value
        if (lat != null && lng != null) LatLng(lat, lng) else null
    }

    val cameraPositionState = rememberCameraPositionState()
    var mapLoaded by remember { mutableStateOf(false) }

    var showDirectPolyline by remember { mutableStateOf(false) }
    var showDrivingPolyline by remember { mutableStateOf(false) }

    val drivingPoints by produceState(initialValue = emptyList(), drivingPolyline) {
        withContext(Dispatchers.Default) {
            value = decodePolyline(drivingPolyline)
        }
    }

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
        stopPositions.forEachIndexed { index, latLng ->
            Marker(
                state = MarkerState(position = latLng),
                title = "Waypoint ${index + 1}"
            )
        }

        if (showDirectPolyline) {
            Polyline(
                points = stopPositions,
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

        LaunchedEffect(mapLoaded, stopPositions) {
            if (mapLoaded && stopPositions.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.builder()
                stopPositions.forEach { boundsBuilder.include(it) }
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
            enabled = stopPositions.isNotEmpty()
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
