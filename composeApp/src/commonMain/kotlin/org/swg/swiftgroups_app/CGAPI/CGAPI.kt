package org.swg.swiftgroups_app.CGAPI

import androidx.compose.runtime.mutableStateOf
import com.multiplatform.webview.cookie.Cookie
import com.vipulasri.kachetor.KachetorStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.AggregateAPI.AggregateGroups
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI
import org.swg.swiftgroups_app.CGAPI.EventProcessing.EventsAPI
import org.swg.swiftgroups_app.CGAPI.Events.CGEvent
import org.swg.swiftgroups_app.CGAPI.Groups.Group
import org.swg.swiftgroups_app.CGAPI.Groups.GroupList
import org.swg.swiftgroups_app.CGAPI.Groups.HomeGroup.ProfileGroupItem
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEvents
import org.swg.swiftgroups_app.DatabaseDriver.DBObject

object CGAPI {
    var databaseFetched = false
    var refetchProfile = mutableStateOf(false)
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
//        request { timeout {
//            requestTimeoutMillis = 0
//            socketTimeoutMillis = 0
//        } }
    }

    var cookieHeader : List<io.ktor.http.Cookie> = emptyList()

    suspend fun grabMyEvents(): UpcomingEvents {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_events_new?view=events_i_am_attending&limit=15&range=0") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Successful response!")

            val upcomingEventData: UpcomingEvents = response.body()

            return upcomingEventData
        } else {
            println("Bad response! Code: ${response.status.value}")
            return UpcomingEvents(0, emptyList(), 0)
        }

    }

    suspend fun grabProfileData(): List<ProfileDataItem> {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_profile") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
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
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
                append(HttpHeaders.CacheControl, CacheControl.MaxAge(maxAgeSeconds = 3600).toString())
            }
        }

        if (response.status.value in 200..299) {
            val loggedIn : String = response.body()

            if(loggedIn.contains("logout")) {
                return emptyList()
            }


            val profileData : List<ProfileDataItem> = response.body()

            return profileData
        } else {
            return emptyList()
        }
    }

    suspend fun fetchEventsData(fetchAll : Boolean = false) {
        val eventAPIEvents : List<CGEvent> = EventsAPI.grabEvents(fetchAll)

        val swiftdataQueries = DBObject.db.swiftdataQueries


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
                println("Data added/updated to DB!")
            }
        }
    }



    suspend fun fetchEvent(eventID : String) : EventSpecificAPI? {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_event_new?id=${eventID}") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            val eventData : EventSpecificAPI = response.body()
            return eventData
        } else {
            return null
        }
    }

    suspend fun fetchMyGroups() : List<ProfileGroupItem> {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v17/mobile_header_groups?search=&all=false") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Group fetched successfully!")
            val groupList : List<ProfileGroupItem> = response.body()
            return groupList
        } else {
            return emptyList()
        }
    }

    suspend fun fetchGroup(groupID : String) : Group? {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_group_new?id=${groupID}") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Group fetched successfully!")
            val groupList : GroupList = response.body()
            return groupList.group.first()
        } else {
            return null
        }
    }

    suspend fun fetchAllGroups() {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_groups_new?limit=1000") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("All Group fetched successfully!")
            val groupList : AggregateGroups = response.body()

            val swiftdataQueries = DBObject.db.swiftdataQueries

            swiftdataQueries.transaction {


                groupList.groups.list.forEach {
                    val groupCategories : List<String> = (it.categories.map { it.name } + it.groupType)
                    swiftdataQueries.insertClub(
                        clubName = it.groupName,
                        clubID = it.clubId,
                        clubUrl = it.clubUrl,
                        clubCategories = groupCategories.joinToString(","),
                        clubBanner = it.coverURL,
                        clubLogo = it.logoUrl,
                        clubStatus = it.status,
                        clubJoinURL = it.join_group_url
                    )
                }
                afterCommit {
                    println("Groups added/updated to DB!")
                }
            }
        }
    }

    fun generateCookieString(cookieList : List<io.ktor.http.Cookie>): String {
        var cookieString = ""

        cookieList.forEach {
            cookieString += "${it.name}=${it.value};"
        }

        return cookieString

    }

    fun convertCookie(ktorCookie : io.ktor.http.Cookie) : Cookie {
        return Cookie(
            name = ktorCookie.name,
            value = ktorCookie.value,
            expiresDate = ktorCookie.expires?.timestamp,
            maxAge = ktorCookie.maxAge?.toLong(),
            domain = ktorCookie.domain,
            path = ktorCookie.path,
        )
    }
}