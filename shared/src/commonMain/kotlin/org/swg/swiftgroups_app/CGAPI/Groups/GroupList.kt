package org.swg.swiftgroups_app.CGAPI.Groups

import kotlinx.serialization.Serializable

@Serializable
data class GroupList(
    val group: List<Group>
)