package org.swg.swiftgroups_app.CGAPI.Groups

import kotlinx.serialization.Serializable

@Serializable
data class GroupItem(
    val groups: List<Group>,
    val heading: String,
    val type: String
)