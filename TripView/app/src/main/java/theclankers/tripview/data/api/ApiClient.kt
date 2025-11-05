package theclankers.tripview.data.api

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import theclankers.tripview.core.Constants.BASE_URL
import theclankers.tripview.data.models.Stop
import theclankers.tripview.data.models.Trip
import theclankers.tripview.data.models.User
import theclankers.tripview.utils.HttpHelper
import java.io.IOException

object ApiClient {

    private val JSON = "application/json".toMediaType()

    // -------------------------------
    // USER ENDPOINTS
    // -------------------------------
    suspend fun getUser(token: String, userId: Int): User {
        val url = "$BASE_URL/user/$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")

        var user: User? = null
        try {
            val json = JSONObject(responseBody)
            user = User(
                userId = json.getInt("user_id"),
                username = json.getString("username"),
                email = json.getString("email"),
                firstName = json.getString("firstname"),
                lastName = json.getString("lastname"),
                likes = json.getString("likes"),
                dislikes = json.getString("dislikes")
            )
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing user JSON: ${e.message}")
        }

        if (user == null) {
            throw IOException("Failed to parse user data")
        }

        Log.d("ApiClient", "Fetched user: $user")

        return user
    }

    suspend fun loginUser(username: String, password: String): String {
        val url = "$BASE_URL/login"
        val bodyJson = JSONObject().apply {
            put("username", username)
            put("password", password)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    // -------------------------------
    // FRIENDSHIP ENDPOINTS
    // -------------------------------

    // -------------------------------
    // TRIP ENDPOINTS
    // -------------------------------

    suspend fun getActiveTrips(token: String, userId: Int): List<Int> {
        val url = "$BASE_URL/get_active_trips/$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")

        val activeTripsList = mutableListOf<Int>()
        try {
            val activeTripsArray = JSONArray(responseBody)
            for (i in 0 until activeTripsArray.length()) {
                // Each trip id is actually an Int, so store it as such
                activeTripsList.add(activeTripsArray.getInt(i))
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing trips JSON: ${e.message}")
        }

        Log.d("ApiClient", "Fetched active trips: $activeTripsList")

        return activeTripsList
    }

    suspend fun getCompletedTrips(token: String, userId: Int): List<Int> {
        val url = "$BASE_URL/get_completed_trips/$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        val completedTripsList = mutableListOf<Int>()
        try {
            val completedTripsArray = JSONArray(responseBody)
            for (i in 0 until completedTripsArray.length()) {
                // Each trip id is actually an Int, so store it as such
                completedTripsList.add(completedTripsArray.getInt(i))
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing trips JSON: ${e.message}")
        }

        Log.d("ApiClient", "Fetched completed trips: $completedTripsList")
        return completedTripsList
    }

    suspend fun getFriendsTrips(token: String, userId: Int): List<Int> {
        val url = "$BASE_URL/get_friends_trips/$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()
        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        val friendsTripsList = mutableListOf<Int>()
        try {
            val friendsTripsArray = JSONArray(responseBody)
            for (i in 0 until friendsTripsArray.length()) {
                friendsTripsList.add(friendsTripsArray.getInt(i))
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing friends trips JSON: ${e.message}")
        }

        Log.d("ApiClient", "Fetched friends trips: $friendsTripsList")

        return friendsTripsList
    }

    suspend fun getTrip(token: String, tripId: Int): Trip {
        val url = "$BASE_URL/trip/$tripId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")

        var trip: Trip? = null
        try {
            val json = JSONObject(responseBody)
            trip = Trip(
                tripId = json.getInt("trip_id"),
                ownerId = json.getInt("owner_id"),
                name = json.getString("name") ?: "",
                description = json.getString("description") ?: "",
                status = json.getString("status"),
                drivingPolyline = json.getString("driving_polyline"),
                drivingPolylineTimestamp = json.getString("driving_polyline_timestamp"),
                stopIds = json.getJSONArray("stop_ids").let { stopIdsArray ->
                    List(stopIdsArray.length()) { index -> stopIdsArray.getInt(index) }
                }
            )
        }
        catch (e: Exception) {
            Log.e("ApiClient", "Error parsing trip JSON: ${e.message}")
        }

        if (trip == null) {
            throw IOException("Failed to parse trip data")
        }
        
        Log.d("ApiClient", "Fetched trip: $trip")

        return trip
    }
    
    suspend fun getStop(token: String, stopId: Int): Stop {
        val url = "$BASE_URL/stop/$stopId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        var stop: Stop? = null
        try {
            val json = JSONObject(responseBody)
            stop = Stop(
                stopId = json.getInt("stop_id"),
                tripId = json.getInt("trip_id"),
                stopType = json.getString("stop_type"),
                latitude = json.getDouble("latitude"),
                longitude = json.getDouble("longitude"),
                name = json.getString("name"),
                completed = json.getBoolean("completed"),
                order = json.getInt("order")
            )
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing stop JSON: ${e.message}")
        }
        if (stop == null) {
            throw IOException("Failed to parse stop data")
        }

        Log.d("ApiClient", "Fetched stop: $stop")

        return stop
    }

    suspend fun updateStopCompleted(token: String, stopId: Int, completed: Boolean): String {
        val url = "$BASE_URL/update_stop_completed/$stopId"
        val bodyJson = JSONObject().apply {
            put("completed", completed)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .put(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.put(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun deleteStop(token: String, tripId: Int, stopId: Int): String {
        val url = "$BASE_URL/trips/$tripId/$stopId"
        val request = Request.Builder()
            .url(url)
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.delete(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")

    }

    suspend fun archiveTrip(token: String, tripId: Int): String {
        val url = "$BASE_URL/trips/$tripId/archive_trip"

        val bodyJson = JSONObject().apply {
            put("status", "archived")
        }.toString()

        val body = bodyJson.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .patch(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.patch(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")

    }

    suspend fun flipStop(token: String, tripId: Int, stopId: Int): String {
        val url = "$BASE_URL/trips/$tripId/$stopId"

        val bodyJson = JSONObject().apply {
            put("completed", "flipped")
        }.toString()

        val body = bodyJson.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .patch(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.patch(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")


    }
}
