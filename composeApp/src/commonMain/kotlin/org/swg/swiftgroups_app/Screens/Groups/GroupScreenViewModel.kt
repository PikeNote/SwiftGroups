package org.swg.swiftgroups_app.Screens.Groups

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Clubs

class GroupScreenViewModel : ScreenModel {

    val groupList = mutableStateListOf<Clubs>()
    var isLoading by mutableStateOf(false)
    var offset = 0L
    var hasMoreClubs = true
    var lastFilter = ""

    init {
        fetchGroups()
    }

    fun fetchGroups(filter : String = "") {
        if(lastFilter != filter) {
            groupList.clear()
            offset=0
        }
        val clubList = DBObject.db.swiftdataQueries.fetchClubs(filter, offset).executeAsList()
        offset += clubList.size
        groupList += clubList
        if(clubList.size < 50) {
            hasMoreClubs = false
        }
    }

    fun loadMoreClubs() {
        if (!isLoading && hasMoreClubs) {
            screenModelScope.launch {
                isLoading = true
                fetchGroups()
                isLoading = false
            }
        }
    }
}