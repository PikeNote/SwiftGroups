package org.swg.swiftgroups_app.Screens.Event


import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.runBlocking
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI


class SingleEventViewModel ( val eventID : Int) : ScreenModel {

    lateinit var eventSpecificAPI : EventSpecificAPI

    init {
        runBlocking {
            eventSpecificAPI = CGAPI.fetchEvent(eventID.toString())!!
        }
    }
}