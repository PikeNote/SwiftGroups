package org.swg.swiftgroups_app.CGAPI.EventAPI

import kotlinx.serialization.Serializable

@Serializable
data class Ticket (
    val quantity : Int,
    val name : String,
    val ticketName : String,
    val amount : String,
    val purchaseDate : String,
    val eventName : String,
    val eventDateTime : String,
    val paypalUid : String,
    val preventCancellation : Int
)