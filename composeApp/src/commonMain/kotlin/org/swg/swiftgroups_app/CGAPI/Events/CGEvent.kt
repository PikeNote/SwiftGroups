package org.swg.swiftgroups_app.CGAPI.Events

class CGEvent () {
    var startTime : String = "0"
    var endTime : String = "0"
    var eventName : String = "N/A"
    var eventDesc : String = "N/A"
    val attendeeCount : Int = 0
    var eventUrl : String = ""
    var eventLocation : String = ""
    val eventPicture : String = ""
    var club : Club? = null
    var eventID: String = ""
    var eventCategory: List<String> = ArrayList()

    fun selfValidate() : Boolean {
        if(startTime == "0" || endTime == "0") {
            return false;
        }

        if(eventName == "N/A") {
            return false;
        }

        if(eventUrl == "") {
            return false;
        }

        if(club == null || eventID == "") {
            return false;
        }

        return true;
    }
}