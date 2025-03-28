package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.cash.sqldelight.Query
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEvents
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEventData
import org.swg.swiftgroups_app.DatabaseDriver.provideDbDriver
import org.swg.swiftgroups_app.db.Database
import org.swg.swiftgroupsapp.db.Events

class EventsViewModel : ScreenModel {
    var upcomingEvents by mutableStateOf(UpcomingEvents(0, emptyList(), 0))
    var events: List<Events> by mutableStateOf(emptyList())
    var isLoading by mutableStateOf(false)
    var hasMoreEvents by mutableStateOf(true)
    lateinit var database: Database;
    var offset = 0L;
    private val pageSize = 50L;

    init {
        addTestEvents()
        runBlocking {
            database = Database(provideDbDriver(Database.Schema))
            fetchData()
        }
    }

    private fun fetchData() {
        screenModelScope.launch {
            getEvents()
        }
    }

    fun loadMoreEvents() {
        if (!isLoading && hasMoreEvents) {
            screenModelScope.launch {
                isLoading = true
                getEvents()
                isLoading = false
            }
        }
    }

    private fun getEvents() {
        screenModelScope.launch {
            val eventsData = database.swiftdataQueries.fetchEvent(offset).executeAsList()
            if (eventsData.isNotEmpty()) {
                offset += eventsData.size
                events += eventsData
                hasMoreEvents = eventsData.size >= pageSize
            } else {
                hasMoreEvents = false
            }
        }
    }

    private fun addTestEvents() {
        val testEvents = List(10) { index ->
            UpcomingEventData(
                attendees_count = 20 + index,
                attending = "yes",
                checkin_attendees = 0,
                connections_more = 0,
                display_map = 1,
                event_date = "Mar ${index + 1}, 2024",
                event_description = "Test event description $index",
                event_display_list = "",
                event_end_date = null,
                event_end_time = "2:00 PM",
                event_end_utc = "",
                event_group = "Test Group ${index % 3}",
                event_group_id = index,
                event_header = null,
                event_id = index,
                event_name = "Test Event $index",
                event_start_time = "1:00 PM",
                event_start_utc = "",
                event_timezone = "EST",
                event_type = "Social",
                friends_more_going = 0,
                interested = 5,
                isLive = if (index == 0) 1 else 0, // First test event is live
                isOnGoing = 0,
                location = "Test Location $index",
                manage = 0,
                notifications_status = 0,
                pdfTicketsUrl = null,
                photoHeight = null,
                photo_url = "/assets/images/event_default.jpg",
                register_url = null,
                registered = null,
                share_url = null,
                showRegisterButton = 1
            )
        }

        upcomingEvents = UpcomingEvents(
            count = testEvents.size,
            list = testEvents,
            next_range = 0
        )
    }
}