package org.swg.swiftgroups_app.CGAPI.Feed

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val file_name: String,
    val firstName: String,
    val id: String,
    val isCommented: Int,
    val isLiked: Int,
    val lastName: String,
    val numberOfComments: Int,
    val numberOfLikes: Int,
    val photoDate: String,
    val photoHeight: Int,
    val photoWidth: Int,
    val photo_id: Int,
    val photo_url: String,
    val small_photo_url: String,
    val studentId: Int,
    val subFolder: String,
    val uid: String
)