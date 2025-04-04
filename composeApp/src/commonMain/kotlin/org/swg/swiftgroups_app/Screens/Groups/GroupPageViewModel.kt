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
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Events

class GroupPageViewModel(private val groupID : String) : ScreenModel
{
    var group : MutableState<Group?> = mutableStateOf(null)
    val upcomingEvents : MutableState<List<Events>> = mutableStateOf(emptyList())

    init {
        val cacheString = fetchCache()
        if(!cacheString.isNullOrEmpty()) {
            group = json.decodeFromString(cacheString)
        }
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
            val jsonData = Json.encodeToString(group.value)
            DBObject.db.swiftdataQueries.updateClubCache(jsonData, groupID)
        }
    }

    private fun fetchUpcomingEvents () {
        upcomingEvents.value = DBObject.db.swiftdataQueries.fetchEventClub(group.value?.name ?: "").executeAsList()
    }

    private fun fetchCache() : String? {
        return DBObject.db.swiftdataQueries.fetchClubCache(groupID).executeAsOneOrNull()
    }

}