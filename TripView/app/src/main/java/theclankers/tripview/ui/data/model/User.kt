package theclankers.tripview.ui.data.model

import kotlinx.serialization.Serializable

// There will be a User class here, defined in the same way as the User object from the ER diagram


@Serializable
data class User(
    val user_id: Int,
    val first_name: String,
    val last_name: String,
    val username: String,
    val likes: String,
    val dislikes: String
)