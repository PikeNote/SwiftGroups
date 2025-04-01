package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.datetime.LocalDate
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Events

class EventsViewModel : ScreenModel {
    var events: List<Events> by mutableStateOf(emptyList())
    var isLoading by mutableStateOf(false)
    var hasMoreEvents by mutableStateOf(true)
    var showLongEvents by mutableStateOf(true)
    var selectedDate by mutableStateOf<LocalDate?>(null)

    private var database = DBObject.db
    private var offset = 0L
    private val pageSize = 50L
    private var currentSearchQuery = ""

    init {
        getEvents()
    }

    fun loadMoreEvents() {
        if (!isLoading && hasMoreEvents) {
            isLoading = true
            getEvents()
            isLoading = false
        }
    }

    private fun getEvents() {
        val eventsData = if(selectedDate != null) {
            database.swiftdataQueries.filterEventsByDate(
                currentSearchQuery.trim(),
                selectedDate.toString(),
                offset
            ).executeAsList()
        } else {
            database.swiftdataQueries.filterEvents(currentSearchQuery.trim(), offset).executeAsList()
        }

        if (eventsData.isNotEmpty()) {
            offset += eventsData.size
            events += eventsData
            hasMoreEvents = eventsData.size >= pageSize
        } else {
            hasMoreEvents = false
        }

    }

    fun filterEvents(searchQuery: String) {
        if (searchQuery == currentSearchQuery) {
            return
        }

        isLoading = true
        currentSearchQuery = searchQuery
        clearList()

        getEvents()
        isLoading = false

    }

    fun toggleLongEvents() {
        showLongEvents = !showLongEvents
        clearList()
        getEvents()
    }

    fun setSelectedDateCal(date: LocalDate?) {
        selectedDate = date
        clearList()
        getEvents()
    }

    private fun clearList() {
        offset = 0
        events = emptyList()
        hasMoreEvents = true
    }

}