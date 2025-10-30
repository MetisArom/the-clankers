package theclankers.tripview.ui.viewmodel

import kotlinx.serialization.Serializable

// Use this ViewModel to grab data related to an individaul Stop
// Pass as input a "stop_id" and it will give access to all relevant stop object varaibles according to the ER diagram


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