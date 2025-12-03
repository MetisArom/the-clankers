package theclankers.tripview.data.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.json.JSONArray
import org.json.JSONObject

// There will be a Trip class here that defines the Trip object, according to the ER diagram

@Serializable
data class Trip(
    val tripId: Int,
    val ownerId: Int,
    val status: String,
    val drivingPolyline: String,
    val drivingPolylineTimestamp: String,
    val name: String,
    val description: String,
    val stopIds: List<Int>,
    val invitedFriends: List<Int>
)

//@Serializable
//data class TripSuggestion(
//    val name: String,
//    val description: String,
//    @Contextual val stopsJSONArray: JSONArray
//)

data class TripSuggestion(
    val name: String,
    val description: String,
    val totalCostEstimate: Int,            // camelCase
    val costBreakdown: String,
    val transportationSummary: String,
    val transportationBreakdown: String,
    val version: Int,
    val stops: List<Stop>,       // parsed list, not JSONArray
    @Contextual val stopsJSONArray: JSONArray // TODO optional if you still need raw JSON
)
