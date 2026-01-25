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
import org.swg.swiftgroups_app.DataStore.UserSettings
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.DatabaseDriver.DBObject

class SingleEventViewModel (private val eventID : Long, userPrefs : UserSettingsPreferences) : ScreenModel {

    val userSettingsPref: UserSettings = userPrefs.settingsFlow.value

    private var _eventSpecificAPI : MutableStateFlow<EventSpecificAPI?> = MutableStateFlow(null)
    var eventSpecificAPI : StateFlow<EventSpecificAPI?> = _eventSpecificAPI.asStateFlow()

    private val _registrationOpen = MutableStateFlow(true)
    val registrationOpen: StateFlow<Boolean> = _registrationOpen.asStateFlow()

    init {
        try {
            val event = DBObject.db.swiftdataQueries.fetchSpecificEvent(eventID).executeAsOneOrNull()

            if (event != null) {
                if (event.userCacheData.isNotEmpty() && userSettingsPref.cacheEvents) {
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
                 _registrationOpen.update { eventSpecificAPI.value?.registration_status?.contains("Registration will only be open")
                     ?: true }

                 if(userSettingsPref.cacheEvents) {
                     DBObject.db.swiftdataQueries.updateCache(Json.encodeToString(cgData), eventID)
                 }
             }
         }

    }
}