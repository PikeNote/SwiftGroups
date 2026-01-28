package org.swg.swiftgroups_app.CGAPI.Feed

import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val Commented: String,
    val already_viewed: Int,
    val bookmarked: Int,
    val clubId: String,
    val commentType: String,
    val commentUid: String,
    val comments: List<Comment>,
    val content: String,
    val deleteButton: Int,
    val feedType: String,
    val feedTypeId: Int,
    val feedTypeName: String,
    val feedWhen: String,
    val id: String,
    val liked: String,
    val nbComments: String,
    val nbLikes: String,
    val number_of_views: Int,
    val photos: List<Photo>?,
    val postDate: String,
    val postTarget: String,
    val subtype: String,
    val uid: String,
    val writeLikeButton: String,
    val writerFirstName: String,
    val writerId: String,
    val writerLastName: String,
    val writerPhoto: String,
    val writerUid: String
)