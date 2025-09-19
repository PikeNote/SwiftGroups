package org.swg.swiftgroups_app.Screens.Event

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.json
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI
import org.swg.swiftgroups_app.DatabaseDriver.DBObject

class SingleEventViewModel (private val eventID : Int) : ScreenModel {

    private var _eventSpecificAPI : MutableStateFlow<EventSpecificAPI?> = MutableStateFlow(null)
    var eventSpecificAPI : StateFlow<EventSpecificAPI?> = _eventSpecificAPI.asStateFlow()

    private val _registrationOpen = MutableStateFlow(true)
    val registrationOpen: StateFlow<Boolean> = _registrationOpen.asStateFlow()

    private val _existingAutoRegister  = MutableStateFlow(true)
    val existingAutoRegister: StateFlow<Boolean> = _existingAutoRegister.asStateFlow()

    init {
        try {
            checkEventAutoRegister()
            val event = DBObject.db.swiftdataQueries.fetchSpecificEvent(eventID.toString()).executeAsOneOrNull()

            if (event != null) {
                if (event.userCacheData.isNotEmpty()) {
                    _eventSpecificAPI.update { json.decodeFromString(event.userCacheData) }
                    _registrationOpen.update { eventSpecificAPI.value?.registration_status?.contains("Registration will only be open")
                        ?: true }
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
                 _eventSpecificAPI.update { cgData }
                 DBObject.db.swiftdataQueries.updateCache(Json.encodeToString(cgData), eventID.toString())
                 _registrationOpen.update { eventSpecificAPI.value?.registration_status?.contains("Registration will only be open")
                     ?: true }
             }
         }

    }

    fun checkEventAutoRegister() {
        val existsRegister = DBObject.db.swiftdataQueries.existsAutoRegister(eventSpecificAPI.value?.event_id.toString()).executeAsOne()
        if(existsRegister) {
            _existingAutoRegister.update {true}
        } else {
            _existingAutoRegister.update {false}
        }
    }
}