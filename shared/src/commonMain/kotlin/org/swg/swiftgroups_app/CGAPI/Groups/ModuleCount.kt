package org.swg.swiftgroups_app.CGAPI.Groups

import kotlinx.serialization.Serializable

@Serializable
data class ModuleCount(
    val count: String,
    val name: String
)