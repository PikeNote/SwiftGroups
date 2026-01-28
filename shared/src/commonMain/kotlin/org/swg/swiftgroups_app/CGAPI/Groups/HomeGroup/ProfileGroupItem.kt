package org.swg.swiftgroups_app.CGAPI.Groups.HomeGroup

import kotlinx.serialization.Serializable

@Serializable
data class ProfileGroupItem(
    val groups: List<ProfileGroup>,
    val heading: String,
    val type: String
)