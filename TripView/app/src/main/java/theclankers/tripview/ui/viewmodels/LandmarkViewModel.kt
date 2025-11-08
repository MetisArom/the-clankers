package theclankers.tripview.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import theclankers.tripview.data.api.ApiClient
import java.io.File
import java.io.IOException

class LandmarkViewModel : ViewModel() {

    val contextText: MutableState<String?> = mutableStateOf(null)
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val errorMessage: MutableState<String?> = mutableStateOf(null)

    fun fetchLandmarkContext(imagePath: String, token: String) {
        val file = File(imagePath)
        if (!file.exists() || !file.isFile) {
            errorMessage.value = "Image file not found: $imagePath"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val responseString = ApiClient.getLandmarkContext(imagePath, token)
                val json = JSONObject(responseString)
                contextText.value = json.optString("context", "No context found.")
            } catch (e: IOException) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}
