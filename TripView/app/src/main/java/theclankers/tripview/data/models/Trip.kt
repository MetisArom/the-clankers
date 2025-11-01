package theclankers.tripview.data.models

import kotlinx.serialization.Serializable

// There will be a Trip class here that defines the Trip object, according to the ER diagram

@Serializable
data class Trip(
    val tripId: Int,
    val ownerId: Int,
    val status: String,
    val drivingPolyline: String
)