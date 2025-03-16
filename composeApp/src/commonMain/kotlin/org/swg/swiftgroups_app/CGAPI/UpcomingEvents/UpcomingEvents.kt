package org.swg.swiftgroups_app.CGAPI.UpcomingEvents

import kotlinx.serialization.Serializable

@Serializable
data class UpcomingEvents(
    val count: Int,
    val list: List<UpcomingEventData>,
    val next_range: Int
)