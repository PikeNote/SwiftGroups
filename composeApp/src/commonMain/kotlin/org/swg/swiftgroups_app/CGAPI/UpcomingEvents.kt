package org.swg.swiftgroups_app.CGAPI

import kotlinx.serialization.Serializable

@Serializable
data class UpcomingEvents(
    val count: Int,
    val list: List<Item0>,
    val next_range: Int
)