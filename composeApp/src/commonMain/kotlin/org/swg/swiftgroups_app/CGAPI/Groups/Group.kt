package org.swg.swiftgroups_app.CGAPI.Groups

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val favoriteLink: String,
    val groupDropdownLink: String,
    val groupID: Int,
    val groupLogin: String,
    val groupName: String,
    val groupType: String,
    val logoUrl: String
)