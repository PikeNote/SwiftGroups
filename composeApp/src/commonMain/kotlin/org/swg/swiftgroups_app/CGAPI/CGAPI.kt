package org.swg.swiftgroups_app.CGAPI

import androidx.compose.runtime.mutableStateOf
import com.multiplatform.webview.cookie.Cookie
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
import io.ktor.http.HttpStatusCode
import io.ktor.http.parseServerSetCookieHeader
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.AggregateAPI.AggregateGroups
import org.swg.swiftgroups_app.CGAPI.Auth.Auth
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI
import org.swg.swiftgroups_app.CGAPI.EventProcessing.EventsAPI
import org.swg.swiftgroups_app.CGAPI.Events.CGEvent
import org.swg.swiftgroups_app.CGAPI.Feed.Button
import org.swg.swiftgroups_app.CGAPI.Feed.Comment
import org.swg.swiftgroups_app.CGAPI.Feed.Feed
import org.swg.swiftgroups_app.CGAPI.Feed.FeedFilterItem
import org.swg.swiftgroups_app.CGAPI.Feed.FeedPostsItem
import org.swg.swiftgroups_app.CGAPI.Groups.Group
import org.swg.swiftgroups_app.CGAPI.Groups.GroupList
import org.swg.swiftgroups_app.CGAPI.Groups.HomeGroup.ProfileGroupItem
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.CGAPI.Profile.UserProfileQRCode
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEvents
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroups_app.DateTimeFormats.DateTimeFormat
import org.swg.swiftgroups_app.SecureStorage.SecureStorage

object CGAPI {
    var databaseFetched = false
    var refetchProfile = mutableStateOf(false)
    val json =  Json {
        ignoreUnknownKeys = true
    }
    val secureVault = SecureStorage()
    val client = HttpClient() {
        followRedirects = false
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

    suspend fun refreshToken(token : String) : Boolean {
        println("Refreshing token... $token")
        val response: HttpResponse = client.get("https://community.case.edu/student_login?api=1&app_device=android&mobile_token=${token}") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Successful token request!")
            val authResponse: Auth = response.body()
            if(authResponse.success) {
                val cookieSetHeaders : List<String> = response.headers.getAll(HttpHeaders.SetCookie).orEmpty()
                val cookieList : List<io.ktor.http.Cookie> = cookieSetHeaders.map { parseServerSetCookieHeader(it) }
                cookieHeader = mergeCookies(cookieHeader, cookieList)
                secureVault.set("authKey", authResponse.mobile_token)
                //secureVault.set("cg_cookie", generateCookieString((cookieHeader)))
                // Prob don't need this .v.
                return true
            }
        } else {
            println("Token request failed! ${response.body<String>()}")
            return false
        }
        return false
    }

    fun mergeCookies(existing: List<io.ktor.http.Cookie>, incoming: List<io.ktor.http.Cookie>): List<io.ktor.http.Cookie> {
        val merged = (existing + incoming)
            .groupBy { it.name }
            .map { (_, cookies) -> cookies.last() } // Keep the latest by name
        return merged
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
            val profileData: List<ProfileDataItem> = response.body()
            DBObject.db.swiftdataQueries.insertModifications("profileData",Json.encodeToString(profileData))
            return profileData
        } else {
            return emptyList()
        }

    }

    suspend fun fetchAppRedirect(
        loginUrl: String,
        cookieHeader: String,
        hopsLeft: Int = 10
    ): String? {
        println("Fetching ---- ${loginUrl}")
        if(loginUrl== "null") return null
        if (hopsLeft <= 0) return null

        val resp: HttpResponse = client.get(loginUrl) {
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Referrer,        "https://www.campusgroups.com/")
                append(HttpHeaders.Cookie,cookieHeader)
                append(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            }
        }

        println("Fetching App Redirect ${resp.status}")
        println(resp.headers[HttpHeaders.Location])
        resp.headers.entries().forEach { (name, values) ->
            println("$name: ${values.joinToString("; ")}")
        }

        return if (resp.status == HttpStatusCode.Found) {
            val location = resp.headers[HttpHeaders.Location] ?: return null

            // if this is the app‐scheme, we’re done
            if (location.startsWith("cgapp://") || location.startsWith("novalsys-cwru://")) {
                location
            } else {
                // otherwise resolve a relative redirect and recurse
                val nextUrl = if (location.startsWith("http")) {
                    location
                } else {
                    // assume same host
                    "https://community.case.edu$location"
                }
                fetchAppRedirect(nextUrl, cookieHeader, hopsLeft - 1)
            }
        } else {
            // not a redirect → no cgapp:// coming
            null
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
                    clubURL = it.club?.clubUrl ?: "",
                    eventTags = it.eventTags.joinToString()
                )
            }
            afterCommit {
                println("Data added/updated to DB!")
            }
        }
    }



    suspend fun fetchEvent(eventID : String) : EventSpecificAPI? {
        try {

            val response: HttpResponse =
                client.get("https://community.case.edu/mobile_ws/v18/mobile_event_new?id=${eventID}") {
                    method = HttpMethod.Get
                    headers {
                        append(HttpHeaders.Host, "community.case.edu")
                        append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
                    }
                }

            if (response.status.value in 200..299) {
                val eventData: EventSpecificAPI = response.body()
                return eventData
            } else {
                return null
            }
        }
        catch (e : Exception) {
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
            println("My groups fetched successfully!")
            val groupList : List<ProfileGroupItem> = response.body()
            DBObject.db.swiftdataQueries.insertModifications("homeMyGroups",Json.encodeToString(groupList))
            return groupList
        } else {
            return emptyList()
        }
    }

    suspend fun fetchProfileQR() : UserProfileQRCode? {
        try {
            val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_qrcode") {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Host, "community.case.edu")
                    append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
                }
            }

            if (response.status.value in 200..299) {
                println("Profile QR code fetched successfully!")
                val profileQR : UserProfileQRCode = response.body()
                return profileQR
            } else {
                return null
            }
        }
        catch (e : Exception) {
            return null
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

    suspend fun fetchFeed(offset : Int, feedID : String = "0") : List<Feed> {
        var feedType = "topic"
        if(feedID == "0") {
            feedType = ""
        }
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_home?feed_type=${feedType}&feed_type_id=${feedID}&v=2&limit=15&range=${offset}") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Feed fetched successfully!")
            val feedList : List<FeedPostsItem> = response.body()
            return if(feedList.isNotEmpty()) {
                feedList.first().feeds
            } else {
                emptyList()
            }
        }
        return emptyList()
    }

    suspend fun fetchFilter() : List<Button> {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_feed_top") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Filter fetched successfully!")
            val filterList : List<FeedFilterItem> = response.body()
            if(filterList.isNotEmpty()) {
                DBObject.db.swiftdataQueries.insertModifications("filterButtons",Json.encodeToString(filterList.first().buttons))
                return filterList.first().buttons
            } else {
                return emptyList()
            }
        }
        return emptyList()
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

    suspend fun postComment(postID : String, text : String) : Boolean {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_comment_post?to_uid=${postID}&comment=${text}&type=feed") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Comment posted!")
            return true
        } else {
            println("Comment failed!")
            return false
        }
    }

    suspend fun getFeedComments(postUID : String) : List<Comment> {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_comments?uid=${postUID}") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            val commentList : List<Comment> = response.body()
            println("Comment posted!")
            return commentList
        } else {
            println("Comment failed!")
            return emptyList()
        }
    }

    suspend fun likeComment(commentID : String, like : Boolean) {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_comment_like?id=${commentID}&like=${if(like) 1 else 0}") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Comment ${commentID} liked!")
        } else {
            println("Comment ${commentID} failed!")
        }
    }

    suspend fun likePost(postID : String, like : Boolean) {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/update_likes?uid=${postID}&like=${if(like) 1 else 0}&type=feed&reaction=%F0%9F%91%8D") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Comment ${postID} liked!")
        } else {
            println("Comment ${postID} failed!")
        }
    }


    suspend fun fetchAllPersonalGroups() {
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_groups_new?view=my_groups") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
            }
        }

        if (response.status.value in 200..299) {
            println("Personal groups fetched successfully!")
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
                    println("Personal groups added/updated to DB!")
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

    fun checkDBExpiry(dbTimeString : String, expiryMin : Int = 60) : Boolean {
        val changedAt = Instant.parse(dbTimeString, DateTimeFormat.db_currentTimestamp)
        val now = Clock.System.now()
        return (now-changedAt).inWholeMinutes <= expiryMin
    }
}