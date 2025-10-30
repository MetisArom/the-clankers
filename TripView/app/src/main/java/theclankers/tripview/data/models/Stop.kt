package theclankers.tripview.data.models

import kotlinx.serialization.Serializable

// There will be a class here that defines the Stop object, according to the ER diagram

@Serializable
data class Stop(
    val stopId: Int,
    val latitude: Double,
    val longitude: Double,
    val tripId: Int,
    val description: String,
    val name: String,
    val order: Int,
    val completed: Boolean,
    val stopType: String,
)