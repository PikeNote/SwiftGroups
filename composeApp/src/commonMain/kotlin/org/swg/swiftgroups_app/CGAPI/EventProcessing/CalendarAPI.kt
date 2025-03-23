package org.swg.swiftgroups_app.CGAPI.EventProcessing

import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.cookieHeader
import org.swg.swiftgroups_app.CGAPI.Events.CGEvent
import org.swg.swiftgroups_app.CGAPI.Events.Club

object CalendarAPI {

    val organizerPattern = Regex("CN=\"([^\"]*)\":([a-zA-Z0-9:.\\\\/]*)")
    val caseURLPattern = Regex("https:\\/\\/community\\.case\\.edu\\/rsvp\\?id=([0-9]+)")
    val dateTimeFormat = LocalDateTime.Format {
        year()
        monthNumber()
        dayOfMonth()
        chars("T")
        hour()
        minute()
        second()
        chars("Z")
    }

    private suspend fun downloadCalendar(url: String) : String? = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse =  CGAPI.client.get(url)  {
                method = HttpMethod.Get
                headers {
                    //append(HttpHeaders.Host, "community.case.edu")
                    append(HttpHeaders.Cookie, cookieHeader)
                }
            }

            if (response.status.value in 200..299) {
                println("Calendar Fetched!")

                return@withContext response.bodyAsText()
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            return@withContext null
        }
    }

    suspend fun processCalendar() : HashMap<String, CGEvent> {
        val eventList : HashMap<String, CGEvent> = hashMapOf()
        val calendarFile : String? = downloadCalendar("https://community.case.edu/ical/cwru/ical_cwru.ics")

        if(calendarFile != null) {
            val lines = calendarFile.split("\n");
            var event = CGEvent()
            lines.forEach {
                val lineString = it.replace("\r", "")
                when {

                    lineString.startsWith("BEGIN:VEVENT") -> event = CGEvent()
                    lineString.startsWith("ORGANIZER;") -> {
                        val organizerMatch = organizerPattern.find(lineString)
                        val clubName = organizerMatch?.groupValues?.get(1);
                        val clubURL = organizerMatch?.groupValues?.get(2);

                        if (clubName != null && clubURL != null) {
                            event.club = Club(clubName, clubURL)
                        }

                    }

                    lineString.startsWith("DTSTART:") -> event.startTime =
                        dateTimeFormat.parse(lineString.substringAfter(":")).toInstant(UtcOffset.ZERO).toString()

                    lineString.startsWith("DTEND:") -> event.endTime =
                        dateTimeFormat.parse(lineString.substringAfter(":")).toInstant(UtcOffset.ZERO).toString()

                    lineString.startsWith("URL") -> {
                        event.eventUrl = lineString.substringAfter(":")
                        val urlMatch = caseURLPattern.find(event.eventUrl);
                        val eventID = urlMatch?.groupValues?.get(1)

                        if (eventID != null) {
                            event.eventID = eventID
                        }

                    }

                    lineString.startsWith("DESCRIPTION:") -> event.eventDesc = lineString.substringAfter(":")
                    lineString.startsWith("LOCATION:") -> event.eventLocation = lineString.substringAfter(":")
                    lineString.startsWith("SUMMARY;") -> event.eventName = lineString.substringAfter(":")
                    lineString.startsWith("CATEGORIES;X-CG-CATEGORY=event_tags") -> event.eventCategory =
                        lineString.substringAfter(":").split(",")

                    lineString.startsWith("END:VEVENT") -> {
                        if (event.selfValidate()) {
                            eventList[event.eventID] = event
                        }
                    }
                }
            }
        }
        return eventList
    }
}