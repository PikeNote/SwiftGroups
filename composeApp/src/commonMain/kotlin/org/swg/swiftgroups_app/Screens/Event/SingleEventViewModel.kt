package org.swg.swiftgroups_app.Screens.Event

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.json
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI
import org.swg.swiftgroups_app.DatabaseDriver.DBObject

class SingleEventViewModel (private val eventID : Int) : ScreenModel {

    var eventSpecificAPI : MutableState<EventSpecificAPI?> = mutableStateOf(null)

    init {
        try {
            val event = DBObject.db.swiftdataQueries.fetchSpecificEvent(eventID.toString()).executeAsOneOrNull()

            if (event != null) {
                if (event.userCacheData.isNotEmpty()) {
                    eventSpecificAPI.value = json.decodeFromString(event.userCacheData)
                }
                updateData()
            }
        } catch (_: Exception) {
            updateData()
        }

    }

     fun updateData() {
         screenModelScope.launch {
             val cgData = CGAPI.fetchEvent(eventID.toString())

             if (eventSpecificAPI.value != cgData && cgData != null) {
                 eventSpecificAPI.value = cgData
                 DBObject.db.swiftdataQueries.updateCache(Json.encodeToString(cgData), eventID.toString())
             }
         }

    }
}