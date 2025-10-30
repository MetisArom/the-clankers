package theclankers.tripview.data.models

import kotlinx.serialization.Serializable

// There will be a User class here, defined in the same way as the User object from the ER diagram


@Serializable
data class User(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val likes: String,
    val dislikes: String
)