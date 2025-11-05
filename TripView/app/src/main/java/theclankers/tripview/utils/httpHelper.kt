package theclankers.tripview.utils

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//If you call these functions they will take care of putting requests in coroutines for you!
object HttpHelper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(0, TimeUnit.MILLISECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    private suspend fun Call.await() = suspendCoroutine { cont ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                cont.resume(response)
            }
            override fun onFailure(call: Call, e: IOException) {
                cont.resumeWithException(e)
            }
        })
    }

    suspend fun get(request: Request): Response {
        val response = client.newCall(request).await()
        return response
    }

    suspend fun post(request: Request): Response {
        val response = client.newCall(request).await()
        return response
    }

    suspend fun delete(request: Request): Response {
        val response = client.newCall(request).await()
        return response
    }

    suspend fun patch(request: Request): Response {
        val response = client.newCall(request).await()
        return response
    }

    suspend fun put(request: Request): Response {
        val response = client.newCall(request).await()
        return response
    }
}