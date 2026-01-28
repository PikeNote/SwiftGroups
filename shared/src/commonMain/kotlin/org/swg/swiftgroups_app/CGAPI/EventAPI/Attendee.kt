package org.swg.swiftgroups_app.CGAPI.EventAPI

import kotlinx.serialization.Serializable

@Serializable
data class Attendee (
    val user_id : Int,
    val mutual_friends: Int,
    val connectionStatus : Int,
    val invited : Int,
    val photo_url : String,
    val subtitle : String?,
    val cover_url : String?,
    val more_information : String?
)