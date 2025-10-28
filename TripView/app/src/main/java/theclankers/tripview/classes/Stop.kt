package theclankers.tripview.classes

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable


@Serializable
data class Stop (
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val stopType: String,
    val order: Int,
    val completed: Boolean
)