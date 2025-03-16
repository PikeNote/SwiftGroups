package org.swg.swiftgroups_app.CGAPI.Profile

import kotlinx.serialization.Serializable

@Serializable
data class LookingForSelection(
    val id: Int,
    val name: String?,
    val value: String
)