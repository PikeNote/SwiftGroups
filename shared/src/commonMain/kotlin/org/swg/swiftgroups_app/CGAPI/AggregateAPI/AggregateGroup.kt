package org.swg.swiftgroups_app.CGAPI.AggregateAPI

import kotlinx.serialization.Serializable

@Serializable
data class AggregateGroup(
    val activityFeedNotRead: Int,
    val categories: List<Category> = emptyList(),
    val clubId: String,
    val clubUrl: String,
    val coverURL: String,
    val groupName: String,
    val groupType: String,
    val id: String,
    val instructions: String,
    val isCloseMembership: Boolean,
    val isMember: String,
    val isOfficer: String,
    val join_group_url: String,
    val logoUrl: String,
    val nbEvents: String,
    val nbMembers: String,
    val nbOfficers: String,
    val signupInstruction: String = "",
    val status: String,
)