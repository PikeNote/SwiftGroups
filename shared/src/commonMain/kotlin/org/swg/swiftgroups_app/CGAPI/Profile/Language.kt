package org.swg.swiftgroups_app.CGAPI.Profile

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val counter: Int,
    val iconUrl: String,
    val id: Int,
    val language: String
)