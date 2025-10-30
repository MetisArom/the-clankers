package theclankers.tripview.ui.viewmodel

import kotlinx.serialization.Serializable

// Use this ViewModel for grabbing state relevant to a specific user.
// For example, pass as input "user_id" and it will return variables like "first_name", "last_name", and "username"

@Serializable
data class User(
    val user_id: Int,
    val first_name: String,
    val last_name: String,
    val username: String,
    val likes: String,
    val dislikes: String
)