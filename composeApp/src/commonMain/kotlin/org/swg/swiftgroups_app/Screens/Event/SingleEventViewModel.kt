package org.swg.swiftgroups_app.Screens.Event


import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI


class SingleEventViewModel ( val eventID : Int) : ScreenModel {

    var eventSpecificAPI : EventSpecificAPI? = null

    init {
        screenModelScope.launch {
            eventSpecificAPI = CGAPI.fetchEvent(eventID.toString())!!
        }
    }
}