package org.swg.swiftgroups_app.Screens.Groups

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.json
import org.swg.swiftgroups_app.CGAPI.Groups.Group
import org.swg.swiftgroups_app.DataStore.UserSettings
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Events

class SingleGroupViewModel(private val groupID : String, userPrefs : UserSettingsPreferences) : ScreenModel
{
    val userSettingsPref: UserSettings = userPrefs.settingsFlow.value
    var group : MutableState<Group?> = mutableStateOf(null)
    val upcomingEvents : MutableState<List<Events>> = mutableStateOf(emptyList())

    init {
        val cacheString = fetchCache()
        try {
            if(!cacheString.isNullOrEmpty() && userSettingsPref.cacheClubs) {
                group = json.decodeFromString(cacheString)
            }
        } catch (_: Exception){}

        screenModelScope.launch {
            updateData()
        }

    }

    private suspend fun updateData() {
        val groupData  = CGAPI.fetchGroup(groupID)

        if(group.value != groupData) {
            group.value = groupData
            fetchUpcomingEvents()


            // Update cache
            val groupCacheData : Group? = group.value
            if(groupCacheData != null) {
                val jsonData = Json.encodeToString(Group.serializer(),groupCacheData)

                if(userSettingsPref.cacheClubs) {
                    DBObject.db.swiftdataQueries.updateClubCache(jsonData, groupID)
                }
            }
        }
    }

    private fun fetchUpcomingEvents () {
        upcomingEvents.value = DBObject.db.swiftdataQueries.fetchEventClub(group.value?.name ?: "").executeAsList()
    }

    private fun fetchCache() : String? {
        return DBObject.db.swiftdataQueries.fetchClubCache(groupID).executeAsOneOrNull()
    }

}