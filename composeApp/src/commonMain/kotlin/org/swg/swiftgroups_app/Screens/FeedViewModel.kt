package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.json
import org.swg.swiftgroups_app.CGAPI.Feed.Button
import org.swg.swiftgroups_app.CGAPI.Feed.Feed
import org.swg.swiftgroups_app.DatabaseDriver.DBObject

class FeedViewModel : ScreenModel {
    val _feedList = MutableStateFlow(emptyList<Feed>())
    val feedList: StateFlow<List<Feed>> = _feedList.asStateFlow()

    val _filterList = MutableStateFlow(emptyList<Button>())
    val filterList: StateFlow<List<Button>> = _filterList.asStateFlow()

    val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()
    
    val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    var offset = 0

    var hasMorePosts by mutableStateOf(true)

    init {
        screenModelScope.launch {
            _isLoading.update{ true }
            _feedList.update {
                CGAPI.fetchFeed(0)
            }

            val filterCache = DBObject.db.swiftdataQueries.fetchModifications("filterButtons").executeAsOneOrNull()

            try {
                if(filterCache != null && CGAPI.checkDBExpiry(filterCache.changed_at, 9000000)) {
                    println("Defaulting to cached filter list")
                    _filterList.update {
                        json.decodeFromString(filterCache.value_)
                    }
                } else {
                    _filterList.update {
                        CGAPI.fetchFilter()
                    }
                }
            } catch (_: Exception) {
                _filterList.update {
                    CGAPI.fetchFilter()
                }
            }


            offset += filterList.value.size

            _isLoading.update{ false }
        }
    }

    fun updateFeed(wipe : Boolean =false) {
        screenModelScope.launch {
            if(wipe) {
                _feedList.update { mutableListOf() }
                offset = 0
                hasMorePosts = true
                _isLoading.update{ true }
            }

            val feedItems = CGAPI.fetchFeed(offset,
                filterList.value[selectedIndex.value].id.toString()
            )

            hasMorePosts = feedItems.size >= 10

            offset += feedItems.size

            _feedList.update {
                it + feedItems
            }

            _isLoading.update{ false }

            if(_isRefreshing.value) {
                _isRefreshing.update { false }
            }
        }
    }

    fun onFilterSelected(index: Int) {
        _selectedIndex.value = index
        updateFeed(wipe = true)
    }
}