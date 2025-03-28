package org.swg.swiftgroups_app.Screens.Groups

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.Groups.Group
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Events

class GroupPageViewModel(private val groupID : String) : ScreenModel
{
    var group : MutableState<Group?> = mutableStateOf(null)
    val upcomingEvents : MutableState<List<Events>> = mutableStateOf(emptyList())

    init {
        screenModelScope.launch {
            updateData()
        }
    }

    private suspend fun updateData() {
        group.value = CGAPI.fetchGroup(groupID)
        fetchUpcomingEvents()
    }

    private fun fetchUpcomingEvents () {
        upcomingEvents.value = DBObject.db.swiftdataQueries.fetchEventClub(group.value?.name ?: "").executeAsList()
    }

}