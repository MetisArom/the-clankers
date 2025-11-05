package theclankers.tripview.data.models

import kotlinx.serialization.Serializable

// There will be a class here that defines the Stop object, according to the ER diagram

@Serializable
data class Stop(
    val stopId: Int,
    val tripId: Int,
    val stopType: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val completed: Boolean,
    val order: Int,
)