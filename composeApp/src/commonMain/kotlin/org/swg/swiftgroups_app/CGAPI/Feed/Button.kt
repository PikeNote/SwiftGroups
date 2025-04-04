package org.swg.swiftgroups_app.CGAPI.Feed

import kotlinx.serialization.Serializable

@Serializable
data class Button(
    val counter: Int,
    val icon_bg_color: String,
    val icon_url: String,
    val id: Int,
    val is_icon: Int,
    val name: String,
    val type: String
)