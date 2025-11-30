package theclankers.tripview.data.models

import androidx.compose.runtime.MutableState
import java.util.UUID
import java.util.UUID.randomUUID

class Chatt(var username: String? = null, var message: MutableState<String>? = null, var id: UUID? = randomUUID(), var timestamp: String? = null)