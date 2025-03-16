package org.swg.swiftgroups_app.CGAPI.Profile

import kotlinx.serialization.Serializable

@Serializable
data class Interest(
    val counter: Int,
    val id: Int,
    val interest: String
)