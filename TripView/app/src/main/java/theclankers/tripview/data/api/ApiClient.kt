package theclankers.tripview.data.api

import android.R.attr.version
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonObject
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
import theclankers.tripview.data.models.TripSuggestion
import theclankers.tripview.data.models.User
import theclankers.tripview.utils.HttpHelper
import java.io.File
import java.io.IOException
import java.net.URLConnection
import kotlin.Int
import kotlin.String



object ApiClient {

    private val JSON = "application/json".toMediaType()

    // -------------------------------
    // USER ENDPOINTS
    // -------------------------------
    suspend fun getUser(token: String, userId: Int): User = withContext(Dispatchers.IO) {
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

        return@withContext user
    }

    suspend fun login(username: String, password: String): LoginResult = withContext(Dispatchers.IO) {
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
        return@withContext LoginResult(userId, accessToken)
    }

    // TODO: Implement signup API function
    // Calls endpoint /signup
    // returns a LoginResult object
    suspend fun signup(username: String, email: String, password: String, firstName: String, lastName: String, likes: String, dislikes: String): LoginResult {
        return LoginResult(0, "") // to prevent compile errors
    }

    // Calls endpoint /edit_user to update information about the currently logged in user
    suspend fun editUser(token: String, username: String, firstName: String, lastName: String, likes: String, dislikes: String): String = withContext(Dispatchers.IO) {
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

        return@withContext responseBody
    }

    // -------------------------------
    // FRIENDSHIP ENDPOINTS
    // -------------------------------

    suspend fun sendFriendRequest(token: String, userId: Int) = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/send_friend_request/$userId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody()) // Empty body
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")

        Log.d("ApiClient", "Sent friend request to user_id=$userId")
    }

    suspend fun acceptFriendRequest(token: String, userId: Int) = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/accept_friend_request/$userId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody()) // Empty body
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")

        Log.d("ApiClient", "Accepted friend request from user_id=$userId")
    }

    suspend fun declineFriendRequest(token: String, userId: Int) = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/decline_friend_request/$userId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody()) // Empty body
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")

        Log.d("ApiClient", "Declined friend request from user_id=$userId")
    }

    suspend fun removeFriend(token: String, userId: Int) = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/remove_friend/$userId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody()) // Empty body
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")

        Log.d("ApiClient", "Removed friend user_id=$userId")
    }

    suspend fun revokeFriendRequest(token: String, userId: Int) = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/revoke_friend_request/$userId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody()) // Empty body
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")

        Log.d("ApiClient", "Revoked friend request to user_id=$userId")
    }

    suspend fun getFriends(token: String, userId: Int): List<Int> = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/get_friends/$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()
        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")

        val friendsList = mutableListOf<Int>()
        try {
            val friendsArray = JSONArray(responseBody)
            for (i in 0 until friendsArray.length()) {
                friendsList.add(friendsArray.getInt(i))
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing friends JSON: ${e.message}")
        }

        Log.d("ApiClient", "Fetched friends: $friendsList")

        return@withContext friendsList
    }

    suspend fun searchFriends(token: String, query: String): List<Int> = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/search_friends?query=${query}"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()
        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        val resultsList = mutableListOf<Int>()
        try {
            val resultsArray = JSONArray(responseBody)
            for (i in 0 until resultsArray.length()) {
                resultsList.add(resultsArray.getInt(i))
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing search friends JSON: ${e.message}")
        } 
        Log.d("ApiClient", "Search friends results: $resultsList")
        return@withContext resultsList
    }

    suspend fun getInvites(token: String, userId: Int): List<Int> = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/get_invites/$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")

        val invitesList = mutableListOf<Int>()
        try {
            val invitesArray = JSONArray(responseBody)
            for (i in 0 until invitesArray.length()) {
                invitesList.add(invitesArray.getInt(i))
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing invites JSON: ${e.message}")
        }

        Log.d("ApiClient", "Fetched invites: $invitesList")

        return@withContext invitesList
    }

    suspend fun getRelationship(token: String, user_id1: Int, user_id2: Int): String? = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/get_relationship/$user_id1/$user_id2"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()
        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        val responseBody = response.body?.string() ?: throw IOException("Empty response")

        Log.d("ApiClient", "Fetched relationship: $responseBody")
        var relationship: String? = null
        try {
            val json = JSONObject(responseBody)
            relationship = json.getString("status")
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing relationship JSON: ${e.message}")
        }
        return@withContext relationship
    }

    // -------------------------------
    // TRIP ENDPOINTS
    // -------------------------------

    suspend fun getActiveTrips(token: String, userId: Int): List<Int> = withContext(Dispatchers.IO) {
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

        return@withContext activeTripsList
    }

    suspend fun getFriendsTrips(token: String, userId: Int): List<Int> = withContext(Dispatchers.IO) {
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

        return@withContext friendsTripsList
    }

    suspend fun getCompletedTrips(token: String, userId: Int): List<Int> = withContext(Dispatchers.IO) {
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
        return@withContext completedTripsList
    }

    // This function calls the endpoint "$BASE_URL/trips/send_form" with the data
    // It receives an object of the structure:
    // { trips: [] }
    suspend fun submitForm(
        token: String,
        destination: String,
        numDays: String,
        stops: String,
        timeline: String,
        numChoices: String
    ): MutableList<TripSuggestion> = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/submit_form"

        val bodyJson = JSONObject().apply {
            put("destination", destination)
            put("num_versions", numChoices)
            put("numDays", numDays)
            put("stops", stops)
            put("timeline", timeline)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request) // <<< use post helper
        if (!response.isSuccessful) {
            val errBody = response.body?.string()
            throw IOException("Request failed: ${response.code} ${response.message} - $errBody")
        }

        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        Log.d("ResponseBody", responseBody)

        val tripSuggestions = mutableListOf<TripSuggestion>()

        try {
            val json = JSONObject(responseBody)
            val tripsJSON = json.getJSONArray("trips")

            for (i in 0 until tripsJSON.length()) {
                val tripObj = tripsJSON.getJSONObject(i)
                val stopsArray = tripObj.getJSONArray("stops")
                val parsedStops = mutableListOf<Stop>()

                for (j in 0 until stopsArray.length()) {
                    val stopObj = stopsArray.getJSONObject(j)

                    parsedStops.add(
                        Stop(
                            stopId = -1,
                            tripId = -1,
                            completed = false,
                            name = stopObj.getString("name"),
                            latitude = stopObj.getDouble("latitude"),
                            longitude = stopObj.getDouble("longitude"),
                            stopType = stopObj.getString("stop_type"),
                            order = stopObj.getInt("order"),
                        )
                    )
                }

                tripSuggestions.add(
                    TripSuggestion(
                        name = tripObj.getString("name"),
                        description = tripObj.getString("description"),
                        stopsJSONArray = stopsArray,
                        totalCostEstimate = tripObj.getInt("total_cost_estimate"),
                        costBreakdown = tripObj.getString("cost_breakdown"),
                        transportationSummary = tripObj.getString("transportation_summary"),
                        transportationBreakdown = tripObj.getString("transportation_breakdown"),
                        stops = parsedStops,
                        version = tripObj.getInt("version"),
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing trip JSON: ${e.message}")
            throw e
        }

        Log.d("ApiClient", "Fetched trip suggestions: $tripSuggestions")

        return@withContext tripSuggestions
    }

    suspend fun llmChatSse(
        tripId: Int,
        message: String,
        model: String,
        token: String,
        onEvent: (role: String, chunk: String) -> Unit,
        onError: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/trips/$tripId/chat_sse"
        val bodyJson = """{"message":${jsonString(message)},"model":${jsonString(model)}}"""
        val requestBody = bodyJson.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "text/event-stream")
            .post(requestBody)
            .build()

        try {
            val response = HttpHelper.post(request)
            if (!response.isSuccessful) {
                onError("Error: ${response.code}")
                return@withContext
            }

            val source = response.body?.source() ?: run {
                onError("No response body from server")
                return@withContext
            }

            while (!source.exhausted()) {
                val line = source.readUtf8Line() ?: continue
                if (line.startsWith("data:")) {
                    val data = line.removePrefix("data:").trim()
                    try {
                        val jsonObj = kotlinx.serialization.json.Json.parseToJsonElement(data).jsonObject
                        if (jsonObj.containsKey("error")) {
                            onError(jsonObj["error"]?.toString() ?: "Unknown error")
                        } else {
                            val role = jsonObj["role"]?.toString()?.trim('"') ?: "model"
                            val content = jsonObj["content"]?.toString()?.trim('"') ?: ""
                            onEvent(role, content)
                            Log.d("SSEChunk", "role=$role, content='$content'")
                        }
                    } catch (e: Exception) {
                        onError("Failed to parse SSE chunk: $data")
                    }
                }
            }
        } catch (e: Exception) {
            onError("Exception during SSE: ${e.localizedMessage ?: "unknown"}")
        }
    }

    // Helper to quote JSON strings safely!
    fun jsonString(str: String) = "\"" + str.replace("\"", "\\\"") + "\""

    // -------------------------------
    // CAMERA ENDPOINTS
    // -------------------------------

    suspend fun getLandmarkContext(
        imagePath: String,
        token: String?,
        latitude: Double,
        longitude: Double
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
            .addFormDataPart("latitude", latitude.toString())
            .addFormDataPart("longitude", longitude.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(multipartBody)


        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }

        val response = HttpHelper.post(request.build())
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

    suspend fun getTrip(token: String, tripId: Int): Trip = withContext(Dispatchers.IO) {
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
                },
                invitedFriends = json.getJSONArray("invited_friends").let { invitedFriendsArray ->
                    List(invitedFriendsArray.length()) { index -> invitedFriendsArray.getInt(index) }
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

        return@withContext trip
    }

    suspend fun getTripStops(token: String, tripId: Int): String = withContext(Dispatchers.IO){
        val url = "$BASE_URL/trips/$tripId/stops"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return@withContext response.body?.string() ?: throw IOException("Empty response")
    }
    suspend fun getStop(token: String, stopId: Int): Stop = withContext(Dispatchers.IO){
        val url = "$BASE_URL/stop/$stopId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        //if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
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
                order = json.getInt("order"),
                address = json.getString("address"),
                hours = json.getString("hours"),
                rating = json.getString("rating"),
                priceRange = json.getString("priceRange"),
                googleMapsUri = json.getString("googleMapsUri")
            )
        } catch (e: Exception) {
            Log.e("ApiClient", "Error parsing stop JSON: ${e.message}")
        }
        if (stop == null) {
            throw IOException("Failed to parse stop data")
        }

        Log.d("ApiClient", "Fetched stop: $stop")

        return@withContext stop
    }

    suspend fun updateStopCompleted(token: String, stopId: Int, completed: Boolean): String = withContext(Dispatchers.IO){
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
        return@withContext response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun addStop(token: String, name: String, latitude: String, longitude:String): String = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/add_stop"
        val bodyJson = JSONObject().apply {
            put("name", name)
            put("latitude", latitude)
            put("longitude", longitude)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()
        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return@withContext response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun deleteStop(token: String, stopId: Int): String = withContext(Dispatchers.IO){
        val url = "$BASE_URL/stops/$stopId"
        val request = Request.Builder()
            .url(url)
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.delete(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return@withContext response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun archiveTrip(token: String, tripId: Int): String {
        val url = "$BASE_URL/trips/$tripId/archive"

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
    suspend fun updateStops(token: String, tripId: Int, stopIds: List<Int>) = withContext(Dispatchers.IO){
        val url = "$BASE_URL/trips/$tripId/stops"

        // Build JSON payload
        val stopsArray = JSONArray().apply {
            stopIds.forEach { stopId ->
                put(stopId)
            }
        }

        val bodyJson = JSONObject().apply {
            put("stopIds", stopsArray)
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

    suspend fun chooseTrip(token: String, trip: JSONObject): String = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/choose_trip"
        
        val request = Request.Builder()
            .url(url)
            .post(trip.toString().toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code} ${response.message}")
        return@withContext response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun inviteFriend(token: String, tripId: Int, userId: Int) {
        // Implement this to call the @app.route('/invite_friend/<int:trip_id>/<int:user_id>', methods=['POST']) endpoint
        val url = "$BASE_URL/invite_friend/$tripId/$userId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody()) // Empty body
            .addHeader("Authorization", "Bearer $token")
            .build()
        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code} ${response.message}")
    }
    suspend fun uninviteFriend(token: String, tripId: Int, userId: Int) {
        // Implement this to call the @app.route('/uninvite_friend/<int:trip_id>/<int:user_id>', methods=['DELETE']) endpoint
        val url = "$BASE_URL/uninvite_friend/$tripId/$userId"
        val request = Request.Builder()
            .url(url)
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()
        val response = HttpHelper.delete(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code} ${response.message}")
    }
}
