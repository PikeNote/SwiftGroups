package org.swg.swiftgroups_app.CGAPI.AggregateAPI

import kotlinx.serialization.Serializable

@Serializable
data class AggregateGroups(
    val groups: GroupList,
)