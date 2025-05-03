package org.swg.swiftgroups_app.CGAPI.Feed

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val commentDate: String,
    val commentHour: String,
    val commentId: String,
    val commentUid: String,
    val content: String,
    val feedUid: String?,
    var iLiked: Int,
    val numberLikes: Int,
    val photoHeight: Int,
    val studentId: Int,
    val writeLikeButton: String?,
    val writeWhen: String,
    val writerFirstName: String,
    val writerLastName: String,
    val writerPhotoUrl: String,
    val writerUid: String
)