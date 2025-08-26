package org.swg.swiftgroups_app.Screens.Groups

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Clubs

class GroupScreenViewModel : ScreenModel {

    var isLoading by mutableStateOf(false)
    var offset = 0L
    var hasMoreClubs = true
    var lastFilter = ""

    val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val _groupList = MutableStateFlow(emptyList<Clubs>())
    val groupList: StateFlow<List<Clubs>> = _groupList.asStateFlow()

    val _selected = MutableStateFlow(false)
    val selected: StateFlow<Boolean> = _selected.asStateFlow()

    fun toggleSelected() {
        _selected.update { !selected.value }
    }


    val displayList: StateFlow<List<Clubs>> =
        combine(groupList, CGAPI.myGroupIDs, selected
        ) { allGroups, myGroupIDs, selected ->
            if (selected) {
                allGroups.filter { it.clubID in myGroupIDs }
            } else {
                allGroups
            }
        }.stateIn(screenModelScope, SharingStarted.Eagerly, emptyList())


    init {
        fetchGroups()
    }

    fun fetchGroups(filter : String = "") {
        if(lastFilter != filter) {
            lastFilter = filter
            _groupList.update { emptyList() }
            offset=0
            hasMoreClubs = true
        }
        val clubList = DBObject.db.swiftdataQueries.fetchClubs(filter, offset).executeAsList()
        offset += clubList.size
        _groupList.getAndUpdate { it + clubList }
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

    fun fetchUpdatedGroups() {
        screenModelScope.launch {
            CGAPI.fetchAllGroups()
            fetchGroups()
            _isRefreshing.update { false }
        }
    }
}