package theclankers.tripview.ui.viewmodel

import kotlinx.serialization.Serializable

// Use this ViewModel for a specific individual Trip.
// It will take as input a "trip_id" and return state variables defined in the ER diagram


@Serializable
data class Trip(
    val trip_id: Int,
    val owner_id: Int,
    val status: String
)