package theclankers.tripview.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import android.Manifest

@Composable
fun rememberCurrentLocation(context: Context): State<Location?> {
    val locationState = remember { mutableStateOf<Location?>(null) }
    val client = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        @SuppressLint("MissingPermission") // Make sure you handle permissions!
        client.lastLocation.addOnSuccessListener { location: Location? ->
            locationState.value = location
        }
    }
    return locationState
}

@Composable
fun LocationPermissionRequest(
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var permissionRequested by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onPermissionResult(granted)
    }
    LaunchedEffect(Unit) {
        if (!permissionRequested) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionRequested = true
        }
    }
}