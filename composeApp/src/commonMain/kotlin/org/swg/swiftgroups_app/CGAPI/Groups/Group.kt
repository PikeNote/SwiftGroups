package org.swg.swiftgroups_app.CGAPI.Groups

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val events_count: String,
    val friends_joined: String? = "",
    val friends_more_joined: Int,
    val group_categories: List<GroupCategory>? = emptyList(),
    val group_cover_url: String,
    val group_logo_url: String,
    val group_type: String,
    val is_member: Int,
    val is_officer: Int,
    val join_group_url: String,
    val members_count: String,
    val mission: String = "",
    val module_counts: List<ModuleCount>,
    val name: String,
    val newsletters_count: String,
    val notifications_status: Int,
    val officers_count: String,
    val photos_count: String,
    val surveys_count: String,
    val websiteUrl: String
)