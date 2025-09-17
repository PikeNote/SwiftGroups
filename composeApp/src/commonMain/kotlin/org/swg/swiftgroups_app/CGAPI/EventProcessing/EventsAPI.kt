package org.swg.swiftgroups_app.CGAPI.EventProcessing

import io.ktor.client.request.headers
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.utils.io.readRemaining
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeSourceToSequence
import org.swg.swiftgroups_app.CGAPI.AggregateAPI.AggregateAPIEventItem
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.cookieHeader
import org.swg.swiftgroups_app.CGAPI.CGAPI.generateCookieString
import org.swg.swiftgroups_app.DatabaseDriver.DBObject

object EventsAPI {

    // Crazy? I was crazy once. They locked me in a rubber room, a rubber room full of rats, and the rats made me go crazy :D
    private val regexMultiDate = Regex("[A-Za-z]+, ([A-Za-z]+) ([0-9]+), ([0-9]{4}) ([0-9]+):?([0-9]+)? ([A-Za-z]+)", RegexOption.MULTILINE)
    private val regexOneDate = Regex("[A-Za-z]+, ([A-Za-z]+) ([0-9]+), ([0-9]{4}) ([0-9]+):?([0-9]+)? ([A-Za-z]+) - ([0-9]+):?([0-9]+)? ([A-Za-z]+)", RegexOption.MULTILINE)
    private val htmlRegex = Regex("(<([^>]+)>)", RegexOption.IGNORE_CASE )
    private val regexEventTag = Regex("<span[^>]*>\\s*<span[^>]*>(.*?)</span>\\s*")

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val dateTimeFormat = LocalDateTime.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        chars(" ")
        dayOfMonth(padding = Padding.NONE)
        chars(" ")
        year()
        chars(" ")
        amPmHour(padding = Padding.NONE)
        chars(" ")
        minute(padding = Padding.NONE)
        chars(" ")
        amPmMarker("AM", "PM")
    }

    private val monthShortFormat = LocalDate.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
    }

    suspend fun grabEvents(offset : Int, limit : Int = 200) {

        val timestamp = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()
        val dateToday = timestamp.toLocalDateTime(tz).date

        val swiftdataQueries = DBObject.db.swiftdataQueries

        try {
            val url = "https://community.case.edu/mobile_ws/v17/mobile_events_list?range=${offset}&limit=${limit}&filter4_contains=OR&timestamp=${timestamp.epochSeconds}&filter8=${dateToday.dayOfMonth}%20${dateToday.format(monthShortFormat)}%20${dateToday.year}&filter4_notcontains=OR&order=undefined&search_word=&&1726272567036"

            CGAPI.backgroundClient.prepareGet(url) {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Host, "community.case.edu")
                    append(HttpHeaders.Cookie, generateCookieString(cookieHeader))
                }
            }.execute { httpResponse ->
                if (httpResponse.status.value !in 200..299) {
                    println(url)
                    println("Events fetching failed! ${httpResponse.status.value}")
                    return@execute
                }

                println("Events ${offset}-${offset+limit} from API fetched!")
                httpResponse.bodyAsSequence<AggregateAPIEventItem>().forEach {
                    if(it.p2 != "separator" && it.p4 != "") {
                        val fixedTime = htmlRegex.replace(it.p4, " ").replace("&ndash;", "-").replace("  ", " ")
                        val convertedTime : List<String> = timeConverter(fixedTime)
                        val eventTags : List<String> = extractEventTags(it.p22)

                        swiftdataQueries.insertEvent(
                            eventName = it.p3,
                            eventDesc = "",
                            eventUrl = it.p18,
                            eventPicture = "https://community.case.edu${it.p11.replace("r2_image_upload", "r3_image_upload")}",
                            eventId = it.p1,
                            eventLocation = it.p6,
                            eventCategory = it.p5.replace(htmlRegex, "\n"),
                            clubName = it.p9,
                            clubURL = "",
                            eventAttendees = it.p10.toLong(),
                            start_time = convertedTime[0],
                            end_time = convertedTime[1],
                            eventTags = eventTags.joinToString()
                        )
                    }
                }
            }

        } catch (e: Exception) {
             println(e.message)
         }
    }

    private fun timeConverter(time : String) : List<String> {
        val matches = regexOneDate.findAll(time)

        if(matches.any()) {
            val singleMatch = matches.first().groupValues
            return listOf(
                dateTimeFormat.parse("${singleMatch[1]} ${singleMatch[2]} ${singleMatch[3]} ${singleMatch[4]} ${singleMatch[5].ifEmpty{"00"}} ${singleMatch[6]}").toInstant(TimeZone.UTC).toString(),
                dateTimeFormat.parse("${singleMatch[1]} ${singleMatch[2]} ${singleMatch[3]} ${singleMatch[7]} ${singleMatch[8].ifEmpty{"00"}} ${singleMatch[9]}").toInstant(TimeZone.UTC).toString()
            )
        } else {
            val multiMatch = regexMultiDate.findAll(time)
            if(multiMatch.count() > 1) {

                return multiMatch.map {
                    dateTimeFormat.parse("${it.groupValues[1]} ${it.groupValues[2]} ${it.groupValues[3]} ${it.groupValues[4]} ${it.groupValues[5].ifEmpty{"00"}} ${it.groupValues[6]}").toInstant(TimeZone.UTC).toString()
                }.toList()
            }
        }
        return emptyList()
    }

    private fun extractEventTags(tags : String) : List<String> {
        val eventTagList = ArrayList<String>()
        val eventTagMatches = regexEventTag.findAll(tags)
        eventTagMatches.forEach {
            if(it.groupValues.size > 1) {
                eventTagList.add(it.groupValues[1])
            }
        }
        return eventTagList
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend inline fun <reified T> HttpResponse.bodyAsSequence(): Sequence<T> {

        val channel = bodyAsChannel()
        return Buffer().use { buffer ->
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(8192L)
                while (!packet.exhausted()) {
                    val bytes = packet.readByteArray()
                    buffer.write(bytes)
                }
            }
            json.decodeSourceToSequence(buffer)
        }
    }
}