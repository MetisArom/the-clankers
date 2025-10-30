package theclankers.tripview.data.api

import kotlinx.serialization.json.Json
import theclankers.tripview.utils.Constants.BASE_URL
import okhttp3.Request
import theclankers.tripview.utils.HttpHelper
import java.io.IOException

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getUser(userId: Int): String {
        val url = "$BASE_URL/users/$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = HttpHelper.get(request)
        if (!response.isSuccessful) {
            throw IOException("Request failed: ${response.code}")
        }
        return response.body.string()
    }
}