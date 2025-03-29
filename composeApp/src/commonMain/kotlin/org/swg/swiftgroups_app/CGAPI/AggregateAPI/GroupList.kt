package org.swg.swiftgroups_app.CGAPI.AggregateAPI

import kotlinx.serialization.Serializable

@Serializable
data class GroupList(
    val count: Int,
    val list: List<AggregateGroup>
)