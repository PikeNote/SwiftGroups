package org.swg.swiftgroups_app.CGAPI.EventAPI

import kotlinx.serialization.Serializable

@Serializable
data class EventTag(
    val id: Int,
    val name: String
)