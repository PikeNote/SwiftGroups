package org.swg.swiftgroups_app.CGAPI.Events

import kotlinx.serialization.Serializable

@Serializable
data class CGEvent (
    var startTime : String = "0",
    var endTime : String = "0",
    var eventName : String = "N/A",
    var eventDesc : String = "N/A",
    var attendeeCount : String = "0",
    var eventUrl : String = "",
    var eventLocation : String = "",
    var eventPicture : String = "",
    var club : Club? = null,
    var eventID: String = "",
    var eventCategory: List<String> = ArrayList(),
    var eventTags : List<String> = ArrayList()
)
/*
{

    fun selfValidate() : Boolean {
        if(startTime == "0" || endTime == "0") {
            return false
        }

        if(eventName == "N/A") {
            return false
        }

        if(eventUrl == "") {
            return false
        }

        if(club == null || eventID == "") {
            return false
        }

        return true
    }
}
 */