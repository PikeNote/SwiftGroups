package org.swg.swiftgroups_app.CGAPI.AggregateAPI

import kotlinx.serialization.Serializable

@Serializable
data class AggregateGroupList(
    val count: Int,
    val list: List<AggregateGroup>
)