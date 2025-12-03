package theclankers.tripview.data.models

import androidx.compose.runtime.MutableState
import java.util.UUID
import java.util.UUID.randomUUID

data class Chatt(
    var username: String,
    var message: MutableState<String>? = null,
    var id: UUID = UUID.randomUUID(),
    var timestamp: String,
    var role: String // "user" or "model"
)
