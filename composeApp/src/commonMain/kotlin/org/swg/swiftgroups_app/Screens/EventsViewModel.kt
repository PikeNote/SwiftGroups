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
    var selectedCategories by mutableStateOf<Set<String>>(emptySet())
    var selectedClubs by mutableStateOf<Set<String>>(emptySet())
    var selectedTags by mutableStateOf<Set<String>>(emptySet())
    var categories by mutableStateOf<List<String>>(emptyList())
    var clubs by mutableStateOf<List<String>>(emptyList())
    var tags by mutableStateOf<List<String>>(emptyList())

    private var database = DBObject.db
    private var offset = 0L
    private val pageSize = 50L
    private var currentSearchQuery = ""

    init {
        getEvents()
        loadCategories()
        loadClubs()
        loadTags()
    }

    private fun loadCategories() {
        categories = database.swiftdataQueries.fetchEventCategories().executeAsList()
    }

    private fun loadClubs() {
        clubs = database.swiftdataQueries.fetchClubNames().executeAsList()
    }

    private fun loadTags() {
        val rawTags = database.swiftdataQueries.fetchEventTags().executeAsList()
        tags = rawTags.flatMap { tagString -> 
            tagString.split(",").map { it.trim() }
        }.distinct().sorted()
    }

    fun loadMoreEvents() {
        if (!isLoading && hasMoreEvents) {
            isLoading = true
            getEvents()
            isLoading = false
        }
    }

    private fun getEvents() {
        val eventsData = when {
            // Date + Categories + Clubs + Tags
            selectedDate != null && selectedCategories.isNotEmpty() && selectedClubs.isNotEmpty() && selectedTags.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByDateAndTagsAndCategoriesAndClubs(
                    currentSearchQuery.trim(),
                    selectedDate.toString(),
                    selectedTags.toList(),
                    selectedCategories.toList(),
                    selectedClubs.toList(),
                    offset
                ).executeAsList()
            }
            // Date + Categories + Tags
            selectedDate != null && selectedCategories.isNotEmpty() && selectedTags.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByDateAndTagsAndCategories(
                    currentSearchQuery.trim(),
                    selectedDate.toString(),
                    selectedTags.toList(),
                    selectedCategories.toList(),
                    offset
                ).executeAsList()
            }
            // Date + Clubs + Tags
            selectedDate != null && selectedClubs.isNotEmpty() && selectedTags.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByDateAndTagsAndClubs(
                    currentSearchQuery.trim(),
                    selectedDate.toString(),
                    selectedTags.toList(),
                    selectedClubs.toList(),
                    offset
                ).executeAsList()
            }
            // Categories + Clubs + Tags
            selectedCategories.isNotEmpty() && selectedClubs.isNotEmpty() && selectedTags.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByTagsAndCategoriesAndClubs(
                    currentSearchQuery.trim(),
                    selectedTags.toList(),
                    selectedCategories.toList(),
                    selectedClubs.toList(),
                    offset
                ).executeAsList()
            }
            // Date + Tags
            selectedDate != null && selectedTags.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByDateAndTags(
                    currentSearchQuery.trim(),
                    selectedDate.toString(),
                    selectedTags.toList(),
                    offset
                ).executeAsList()
            }
            // Categories + Tags
            selectedCategories.isNotEmpty() && selectedTags.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByTagsAndCategories(
                    currentSearchQuery.trim(),
                    selectedTags.toList(),
                    selectedCategories.toList(),
                    offset
                ).executeAsList()
            }
            // Clubs + Tags
            selectedClubs.isNotEmpty() && selectedTags.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByTagsAndClubs(
                    currentSearchQuery.trim(),
                    selectedTags.toList(),
                    selectedClubs.toList(),
                    offset
                ).executeAsList()
            }
            // Just Tags
            selectedTags.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByTags(
                    currentSearchQuery.trim(),
                    selectedTags.toList(),
                    offset
                ).executeAsList()
            }
            // Date + Categories + Clubs
            selectedDate != null && selectedCategories.isNotEmpty() && selectedClubs.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByDateAndClubsAndCategories(
                    currentSearchQuery.trim(),
                    selectedDate.toString(),
                    selectedClubs.toList(),
                    selectedCategories.toList(),
                    offset
                ).executeAsList()
            }
            // Date + Categories
            selectedDate != null && selectedCategories.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByDateAndCategories(
                    currentSearchQuery.trim(),
                    selectedDate.toString(),
                    selectedCategories.toList(),
                    offset
                ).executeAsList()
            }
            // Date + Clubs
            selectedDate != null && selectedClubs.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByDateAndClubs(
                    currentSearchQuery.trim(),
                    selectedDate.toString(),
                    selectedClubs.toList(),
                    offset
                ).executeAsList()
            }
            // Categories + Clubs
            selectedCategories.isNotEmpty() && selectedClubs.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByClubsAndCategories(
                    currentSearchQuery.trim(),
                    selectedClubs.toList(),
                    selectedCategories.toList(),
                    offset
                ).executeAsList()
            }
            // Just Date
            selectedDate != null -> {
            database.swiftdataQueries.filterEventsByDate(
                currentSearchQuery.trim(),
                selectedDate.toString(),
                offset
            ).executeAsList()
            }
            // Just Categories
            selectedCategories.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByCategories(
                    currentSearchQuery.trim(),
                    selectedCategories.toList(),
                    offset
                ).executeAsList()
            }
            // Just Clubs
            selectedClubs.isNotEmpty() -> {
                database.swiftdataQueries.filterEventsByClubs(
                    currentSearchQuery.trim(),
                    selectedClubs.toList(),
                    offset
                ).executeAsList()
            }
            // No filters
            else -> {
            database.swiftdataQueries.filterEvents(currentSearchQuery.trim(), offset).executeAsList()
            }
        }

        if (eventsData.isNotEmpty()) {
            offset += eventsData.size
            events = events + eventsData
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

    fun updateSelectedCategories(categories: Set<String>) {
        selectedCategories = categories
        clearList()
        getEvents()
    }

    fun updateSelectedClubs(clubs: Set<String>) {
        selectedClubs = clubs
        clearList()
        getEvents()
    }

    fun updateSelectedTags(tags: Set<String>) {
        selectedTags = tags
        clearList()
        getEvents()
    }

    private fun clearList() {
        offset = 0
        events = emptyList()
        hasMoreEvents = true
    }
}