package theclankers.tripview.data.network

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
import org.json.JSONObject
import theclankers.tripview.core.Constants.BASE_URL
import theclankers.tripview.utils.HttpHelper
import java.io.File
import java.io.IOException
import java.net.URLConnection

object ApiClient {

    private val JSON = "application/json".toMediaType()

    // -------------------------------
    // USER ENDPOINTS
    // -------------------------------
    suspend fun getUser(token: String, userId: Int): String {
        val url = "$BASE_URL/user/$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun createUser(
        username: String,
        password: String,
        fullname: String,
        likes: String? = null,
        dislikes: String? = null
    ): String {
        val url = "$BASE_URL/create_user"
        val bodyJson = JSONObject().apply {
            put("username", username)
            put("password", password)
            put("fullname", fullname)
            likes?.let { put("likes", it) }
            dislikes?.let { put("dislikes", it) }
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
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

    suspend fun editUser(
        token: String,
        firstname: String,
        lastname: String,
        likes: String?,
        dislikes: String?
    ): String {
        val url = "$BASE_URL/edit_user"
        val bodyJson = JSONObject().apply {
            put("firstname", firstname)
            put("lastname", lastname)
            likes?.let { put("likes", it) }
            dislikes?.let { put("dislikes", it) }
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    // -------------------------------
    // FRIENDSHIP ENDPOINTS
    // -------------------------------

    suspend fun getFriends(token: String): String {
        val url = "$BASE_URL/friends"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun sendFriendRequest(token: String, userId: Int): String {
        val url = "$BASE_URL/friends/request"
        val bodyJson = JSONObject().apply { put("user_id", userId) }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun acceptFriendRequest(token: String, userId: Int): String {
        val url = "$BASE_URL/friends/accept"
        val bodyJson = JSONObject().apply { put("user_id", userId) }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun declineFriendRequest(token: String, userId: Int): String {
        val url = "$BASE_URL/friends/decline"
        val bodyJson = JSONObject().apply { put("user_id", userId) }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun searchUsers(token: String, query: String): String {
        val url = "$BASE_URL/friends/search?q=$query"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    // -------------------------------
    // TRIP ENDPOINTS
    // -------------------------------

    suspend fun createTrip(
        token: String,
        destination: String,
        numVersions: Int,
        likes: String? = null,
        dislikes: String? = null
    ): String {
        val url = "$BASE_URL/trips/create"
        val bodyJson = JSONObject().apply {
            put("destination", destination)
            put("num_versions", numVersions)
            likes?.let { put("likes", it) }
            dislikes?.let { put("dislikes", it) }
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun getTrip(token: String, tripId: Int): String {
        val url = "$BASE_URL/trips/$tripId/info"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun getTripStops(token: String, tripId: Int): String {
        val url = "$BASE_URL/trips/$tripId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
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

    suspend fun inviteMember(token: String, friendId: Int, tripId: Int): String {
        val url = "$BASE_URL/party/invite"
        val bodyJson = JSONObject().apply {
            put("friend_id", friendId)
            put("trip_id", tripId)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun removeMember(token: String, friendId: Int, tripId: Int): String {
        val url = "$BASE_URL/party/remove"
        val bodyJson = JSONObject().apply {
            put("friend_id", friendId)
            put("trip_id", tripId)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.post(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }

    suspend fun viewPartyMembers(token: String, tripId: Int): String {
        val url = "$BASE_URL/party/$tripId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) throw IOException("Request failed: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response")
    }
}
