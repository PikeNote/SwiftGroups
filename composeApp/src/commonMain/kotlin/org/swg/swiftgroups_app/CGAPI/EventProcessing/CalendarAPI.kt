package org.swg.swiftgroups_app.CGAPI.EventProcessing

import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.byUnicodePattern
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.Events.CGEvent
import org.swg.swiftgroups_app.CGAPI.Events.Club

object CalendarAPI {

    val organizerPattern = Regex("CN=\"([^\"]*)\":([a-zA-Z0-9:.\\\\/]*)")
    val caseURLPattern = Regex("https:\\/\\/community\\.case\\.edu\\/rsvp\\?id=([0-9]+)")
    val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("yyyyMMdd'T'HHmmss'Z'")
    }

    private suspend fun downloadCalendar(url: String) : String? = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse =  CGAPI.client.get(url)

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
                when {
                    it.startsWith("BEGIN:VEVENT") -> event = CGEvent()
                    it.startsWith("ORGANIZER;") -> {
                        val organizerMatch = organizerPattern.find(it)
                        val clubName = organizerMatch?.groupValues?.get(1);
                        val clubURL = organizerMatch?.groupValues?.get(2);

                        if (clubName != null && clubURL != null) {
                            event.club = Club(clubName, clubURL)
                        }

                    }

                    it.startsWith("DTSTART:") -> event.startTime =
                        dateTimeFormat.parse(it.substringAfter(":")).toString()

                    it.startsWith("DTEND:") -> event.endTime =
                        dateTimeFormat.parse(it.substringAfter(":")).toString()

                    it.startsWith("URL") -> {
                        event.eventUrl = it.substringAfter(":")
                        val urlMatch = caseURLPattern.find(event.eventUrl);
                        val eventID = urlMatch?.groupValues?.get(1)

                        if (eventID != null) {
                            event.eventID = eventID
                        }

                    }

                    it.startsWith("DESCRIPTION:") -> event.eventDesc = it.substringAfter(":")
                    it.startsWith("LOCATION:") -> event.eventLocation = it.substringAfter(":")
                    it.startsWith("SUMMARY;") -> event.eventName = it.substringAfter(":")
                    it.startsWith("CATEGORIES;X-CG-CATEGORY=event_tags") -> event.eventCategory =
                        it.substringAfter(":").split(",")

                    it.startsWith("END:VEVENT") -> {
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