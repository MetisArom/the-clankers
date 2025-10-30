package theclankers.tripview.data.api

import kotlinx.serialization.json.Json
import theclankers.tripview.utils.Constants.BASE_URL

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getUser(userId: Int): String

}