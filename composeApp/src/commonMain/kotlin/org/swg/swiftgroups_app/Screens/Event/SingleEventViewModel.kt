package org.swg.swiftgroups_app.Screens.Event


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI
import org.swg.swiftgroups_app.DatabaseDriver.DBObject


class SingleEventViewModel (private val eventID : Int) : ScreenModel {

    var eventSpecificAPI : MutableState<EventSpecificAPI?> = mutableStateOf(null)

    init {
        screenModelScope.launch {
            val event = DBObject.db.swiftdataQueries.fetchSpecificEvent(eventID.toLong()).executeAsOne()

            if(event.userCacheData.isNotEmpty()) {
                eventSpecificAPI.value = Json.decodeFromString(event.userCacheData)
            }

            updateData()
        }
    }

    suspend fun updateData() {
        val cgData = CGAPI.fetchEvent(eventID.toString())

        if (eventSpecificAPI.value != cgData) {
            eventSpecificAPI.value = cgData
            DBObject.db.swiftdataQueries.updateCache(Json.encodeToString(cgData), eventID.toLong())

        }
    }
}