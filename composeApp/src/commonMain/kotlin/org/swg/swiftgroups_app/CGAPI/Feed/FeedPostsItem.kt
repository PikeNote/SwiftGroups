package org.swg.swiftgroups_app.CGAPI.Feed

import kotlinx.serialization.Serializable

@Serializable
data class FeedPostsItem(
    val feeds: List<Feed>,
    val forum_id: String,
    val forum_recent_count: String,
    val linkCounter: String,
    val linkName: String,
    val linkUrl: String,
    val poll_id: String,
    val poll_question: String,
    val showPostIcon: String
)