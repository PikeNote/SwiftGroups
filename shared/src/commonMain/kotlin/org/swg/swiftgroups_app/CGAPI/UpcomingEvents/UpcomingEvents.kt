package org.swg.swiftgroups_app.CGAPI.UpcomingEvents

import kotlinx.serialization.Serializable

@Serializable
data class UpcomingEvents(
    val count: Int = 0,
    val list: List<UpcomingEventData> = emptyList(),
    val next_range: Int = 0
)