package theclankers.tripview.data.api

import android.R.attr.password
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import theclankers.tripview.core.Constants.BASE_URL
import theclankers.tripview.data.models.LoginResult
import theclankers.tripview.data.models.Stop
import theclankers.tripview.data.models.Trip
import theclankers.tripview.data.models.User
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.utils.HttpHelper
import java.io.File
import java.io.IOException
import java.net.URLConnection

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

    suspend fun login(username: String, password: String): LoginResult {
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

        val responseBody = response.body?.string() ?: throw IOException("Empty response")

        var userId: Int? = null
        var accessToken: String? = null
        try {
            val json = JSONObject(responseBody)
            userId = json.getInt("user_id")
            accessToken = json.getString("access_token")
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing login JSON: ${e.message}")
        }

        if (userId == null || accessToken == null) {
            throw IOException("Failed to parse login data")
        }

        Log.d("ApiClient", "Login successful: user_id=$userId")

        // This will return both userId and accessToken as a LoginResult object
        return LoginResult(userId, accessToken)
    }

    // TODO: Implement signup API function
    // Calls endpoint /signup
    // returns a LoginResult object
    suspend fun signup(username: String, email: String, password: String, firstName: String, lastName: String, likes: String, dislikes: String): LoginResult {
        return LoginResult(0, "") // to prevent compile errors
    }

    // Calls endpoint /edit_user to update information about the currently logged in user
    suspend fun editUser(token: String, username: String, firstName: String, lastName: String, likes: String, dislikes: String): String {
        val url = "$BASE_URL/edit_user"

        val bodyJson = JSONObject().apply {
            put("username", username)
            put("firstname", firstName)
            put("lastname", lastName)
            put("likes", likes)
            put("dislikes", dislikes)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code} ${response.message}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")

        Log.d("ApiClient", "Updated currently logged in user!")

        return responseBody
    }

    // -------------------------------
    // FRIENDSHIP ENDPOINTS
    // -------------------------------

    // TODO: Implement getFriends, call the back-end route get_friends/<int:user_id>
    suspend fun getFriends(token: String, userId: Int): List<Int> {
        return emptyList()
    }

    // TODO: Implement getInvites, call the back-end route get_invites/<int:user_id>
    suspend fun getInvites(token: String, userId: Int): List<Int> {
        return emptyList()
    }

    // TODO: Implement getRelationship, call the back-end route get_relationship/<int:user_id1>/<int:user_id2>
    // get_relationship returns a status string: "friends", "pending_incoming", "pending_outgoing", "none", "self"
    suspend fun getRelationship(token: String, user_id1: Int, user_id2: Int): String {
        return ""
    }

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

    // -------------------------------
    // CAMERA ENDPOINTS
    // -------------------------------

    suspend fun landmarkContext(
        imagePath: String
    ): String = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/landmark_context"

        val file = File(imagePath)
        if(!file.exists() || !file.isFile) {
            throw IOException("Image file not found: $imagePath")
        }

        val guess = URLConnection.guessContentTypeFromName(file.name)
        val mimeType = guess?: "image/jpeg"

        val fileRequestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, fileRequestBody)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(multipartBody)
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return@withContext response.body?.string() ?: throw IOException("Empty response")
    }

    // -------------------------------
    // PARTY MANAGEMENT ENDPOINTS
    // -------------------------------

//    suspend fun inviteMember(token: String, friendId: Int, tripId: Int): String {
//        val url = "$BASE_URL/party/invite"
//        val bodyJson = JSONObject().apply {
//            put("friend_id", friendId)
//            put("trip_id", tripId)
//        }.toString()
//
//        val request = Request.Builder()
//            .url(url)
//            .get()
//            .addHeader("Authorization", "Bearer $token")
//            .build()
//        val response = HttpHelper.get(request)
//        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
//        val responseBody = response.body?.string() ?: throw IOException("Empty response")
//        val friendsTripsList = mutableListOf<Int>()
//        try {
//            val friendsTripsArray = JSONArray(responseBody)
//            for (i in 0 until friendsTripsArray.length()) {
//                friendsTripsList.add(friendsTripsArray.getInt(i))
//            }
//        } catch (e: Exception) {
//            Log.e("ApiClient", "Error parsing friends trips JSON: ${e.message}")
//        }
//
//        Log.d("ApiClient", "Fetched friends trips: $friendsTripsList")
//
//        return friendsTripsList
//    }

    suspend fun getTrip(token: String, tripId: Int): Trip {
        val url = "$BASE_URL/trip/$tripId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code} ${response.message}")
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

    suspend fun getTripStops(token: String, tripId: Int): String {
        val url = "$BASE_URL/trips/$tripId/stops"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
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
        print(responseBody)
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

    suspend fun deleteStop(token: String, stopId: Int): String {
        val url = "$BASE_URL/stops/$stopId"
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
    suspend fun updateStops(token: String, tripId: Int, stops: List<Stop>) {
        val url = "$BASE_URL/trips/$tripId/stops"

        // Build JSON payload
        val stopsArray = JSONArray(stops.mapIndexed { index, stop ->
            JSONObject().apply {
                put("stop_id", stop.stopId)            // matches backend
                put("order", index)               // matches backend
                put("completed", stop.completed)       // boolean
                put("description", stop.name)          // backend uses 'description'
                put("stop_type", stop.stopType)        // optional if you need
                put("latitude", stop.latitude)
                put("longitude", stop.longitude)
            }
        })

        val bodyJson = JSONObject().apply {
            put("stops", stopsArray)
        }.toString()

        println("PATCH payload: $bodyJson")

        val request = Request.Builder()
            .url(url)
            .patch(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.patch(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
    }

}
