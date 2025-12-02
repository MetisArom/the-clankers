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
    val stopIds: List<Int>
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
    val stops: List<SuggestedStop>,       // parsed list, not JSONArray
    @Contextual val stopsJSONArray: JSONArray // optional if you still need raw JSON
)

// Each suggested stop from the LLM JSON
data class SuggestedStop(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val order: Int,
    val stopType: String
)

