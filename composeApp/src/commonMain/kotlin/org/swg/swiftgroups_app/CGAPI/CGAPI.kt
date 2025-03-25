package org.swg.swiftgroups_app.CGAPI

import com.vipulasri.kachetor.KachetorStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI
import org.swg.swiftgroups_app.CGAPI.EventProcessing.EventsAPI
import org.swg.swiftgroups_app.CGAPI.Events.CGEvent
import org.swg.swiftgroups_app.CGAPI.Groups.GroupItem
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEvents
import org.swg.swiftgroups_app.DatabaseDriver.provideDbDriver
import org.swg.swiftgroups_app.db.Database


object CGAPI {
    val client = HttpClient() {
        install(ContentNegotiation) {
            json(contentType = ContentType.Any, json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(HttpCache) {
            publicStorage(KachetorStorage(10 * 1024 * 1024))
        }
    }

    var cookieHeader = ""

    suspend fun grabMyEvents(): UpcomingEvents {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_events_new?view=events_i_am_attending&limit=15&range=0") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, cookieHeader)
            }
        }

        if (response.status.value in 200..299) {
            println("Successful response!")

            val upcomingEventData: UpcomingEvents = response.body()

            return upcomingEventData
        } else {
            return UpcomingEvents(0, emptyList(), 0)
        }

    }

    suspend fun grabProfileData(): List<ProfileDataItem> {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_profile") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, cookieHeader)
            }
        }

        if (response.status.value in 200..299) {
            val upcomingEventData: List<ProfileDataItem> = response.body()

            return upcomingEventData
        } else {
            return emptyList()
        }

    }

    suspend fun checkLoggedIn() : List<ProfileDataItem> {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_profile") {
            // {"logout":true}
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, cookieHeader)
            }
        }

        if (response.status.value in 200..299) {
            val loggedIn : String = response.body()

            if(loggedIn.contains("logout")) {
                return emptyList();
            }


            val profileData : List<ProfileDataItem> = response.body()

            return profileData
        } else {
            return emptyList()
        }
    }

    suspend fun fetchEventsData(fetchAll : Boolean = false) {
        //val calendarEvents : HashMap<String,CGEvent> = CalendarAPI.processCalendar();
        val eventAPIEvents : List<CGEvent> = EventsAPI.grabEvents(fetchAll);

        /*
        eventAPIEvents.forEach {
            if(calendarEvents.containsKey(it.eventID)) {
                val event = calendarEvents[it.eventID]
                if (event != null) {
                    event.eventPicture = it.eventPicture
                    event.eventName = it.eventName
                    event.attendeeCount = it.attendeeCount
                    event.eventLocation = it.eventLocation
                }
            } else {
                calendarEvents[it.eventID] = it
            }
        }*/

        val swiftdataQueries = Database(provideDbDriver(Database.Schema)).swiftdataQueries


        swiftdataQueries.transaction {

            eventAPIEvents.forEach {
                swiftdataQueries.insertEvent(
                    eventId = it.eventID.toLong(),
                    eventName = it.eventName,
                    eventDesc = it.eventDesc,
                    eventUrl = it.eventUrl,
                    eventLocation = it.eventLocation,
                    eventPicture = "https://community.case.edu${it.eventPicture}",
                    eventCategory = it.eventCategory.joinToString(),
                    start_time = it.startTime,
                    end_time = it.endTime,
                    eventAttendees = it.attendeeCount.toLong(),
                    clubName = it.club?.clubName ?: "",
                    clubURL = it.club?.clubUrl ?: ""
                )
            }
            afterCommit {
                println("Data added/updated to DB!");
            }
        }
    }



    suspend fun fetchEvent(eventID : String) : EventSpecificAPI? {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_event_new?id=${eventID}") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, cookieHeader)
            }
        }

        if (response.status.value in 200..299) {
            val eventData : EventSpecificAPI = response.body()
            return eventData
        } else {
            return null
        }
    }

    suspend fun fetchGroups() : List<GroupItem> {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v17/mobile_header_groups?search=&all=false") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, cookieHeader)
            }
        }

        if (response.status.value in 200..299) {
            println("Group fetched successfully!")
            val groupHome : List<GroupItem> = response.body()
            return groupHome
        } else {
            return emptyList()
        }
    }
}