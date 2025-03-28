package org.swg.swiftgroups_app.CGAPI.Groups

import kotlinx.serialization.Serializable

@Serializable
data class GroupCategory(
    val category_id: Int,
    val icon_url: String? = "",
    val name: String
)