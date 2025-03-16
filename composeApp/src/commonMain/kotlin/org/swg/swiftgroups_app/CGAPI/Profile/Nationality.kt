package org.swg.swiftgroups_app.CGAPI.Profile

import kotlinx.serialization.Serializable

@Serializable
data class Nationality(
    val counter: Int,
    val id: Int,
    val nationality: String
)