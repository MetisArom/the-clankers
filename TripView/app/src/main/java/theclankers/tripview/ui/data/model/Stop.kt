package theclankers.tripview.ui.data.model

import kotlinx.serialization.Serializable

// There will be a class here that defines the Stop object, according to the ER diagram

@Serializable
data class Stop(
    val stop_id: Int,
    val trip_id: Int,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val name: String,
    val order: Int,
)