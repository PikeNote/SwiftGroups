package org.swg.swiftgroups_app.CGAPI.EventProcessing

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.AggregateEvents.AggregateAPIEventItem
import org.swg.swiftgroups_app.CGAPI.Events.CGEvent
import org.swg.swiftgroups_app.CGAPI.Events.Club
import org.swg.swiftgroups_app.db.Database

object EventsAPI {

    // Crazy? I was crazy once. They locked me in a rubber room, a rubber room full of rats, and the rats made me go crazy :D
    val regexMultiDate = Regex("/[A-Za-z]+, ([A-Za-z]+) ([0-9]+), ([0-9]{4}) ([0-9]+):?([0-9]+)? ([A-Za-z]+)/gm");
    private val regexOneDate = Regex("/[A-Za-z]+, ([A-Za-z]+) ([0-9]+), ([0-9]{4}) ([0-9]+):?([0-9]+)? ([A-Za-z]+) – ([0-9]+):?([0-9]+)? ([A-Za-z]+)/gm")
    private val categoryRegex = Regex("/(<([^>]+)>)/ig");

    val dateTimeFormat = LocalDateTime.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        dayOfMonth()
        year()
        hour()
        minute()
        amPmMarker("AM", "PM")
    }

    private val monthShortFormat = LocalDate.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
    }



    suspend fun processEvents() {

    }

    suspend fun grabEvents(grabEntire : Boolean = false) : List<CGEvent> {

        val timestamp = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()
        val dateToday = timestamp.toLocalDateTime(tz).date

        var url = "https://community.case.edu/mobile_ws/v17/mobile_events_list?range=0&limit=1000&filter4_contains=OR&timestamp=${timestamp.epochSeconds}&filter8=${dateToday.dayOfMonth} ${dateToday.format(monthShortFormat).toString()} ${dateToday.year}&filter4_notcontains=OR&order=undefined&search_word=&&1726272567036"

        if(grabEntire) {
            url = "https://community.case.edu/mobile_ws/v17/mobile_events_list?range=0&limit=1000&filter4_contains=OR&timestamp=${timestamp.epochSeconds}&filter4_notcontains=OR&order=undefined&search_word=&&1726272567036"
        }
        try {
            val response: HttpResponse =  CGAPI.client.get(url)

            if (response.status.value in 200..299) {
                println("Events API Fetched!")

                val cgAPIItems : List<AggregateAPIEventItem> = response.body()
                val eventItems : MutableList<CGEvent> = mutableListOf()

                Database
                cgAPIItems.forEach {
                    val convertedTime : List<String> = timeConverter(it.p4);
                    eventItems.add(CGEvent(
                        eventName = it.p3,
                        eventDesc = "",
                        eventUrl = it.p18,
                        eventPicture = it.p11.replace("r2_image_upload", "r3_image_upload"),
                        eventID = it.p1,
                        eventLocation = it.p6,
                        eventCategory = it.p5.replace(categoryRegex, "\n").split('\n').filterNotNull(),
                        club = Club(clubName = it.p9, clubUrl = ""),
                        attendeeCount = it.p10,
                        startTime = convertedTime[0],
                        endTime = convertedTime[1]
                    ))
                }



                return eventItems;
            } else {
                return emptyList()
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    suspend fun timeConverter(time : String) : List<String> {
        val matches = regexOneDate.findAll(time)

        if(matches.any()) {
            val singleMatch = matches.first().groupValues
            return listOf(
                dateTimeFormat.parse("${singleMatch[1]}${singleMatch[2]}${singleMatch[3]}${singleMatch[4]}${singleMatch[5].ifEmpty{"00"}}${singleMatch[6]}").toInstant(TimeZone.UTC).toString(),
                dateTimeFormat.parse("${singleMatch[1]}${singleMatch[2]}${singleMatch[3]}${singleMatch[7]}${singleMatch[8].ifEmpty{"00"}}${singleMatch[9]}").toInstant(TimeZone.UTC).toString()
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

}