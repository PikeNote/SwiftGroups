package org.swg.swiftgroups_app.CGAPI.EventProcessing

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGEvent.CGAPIEventItem

object EventsAPI {

    private val monthShortFormat = LocalDate.Format {
        byUnicodePattern("MMM")
    }



    suspend fun processEvents() {

    }

    suspend fun grabEvents(grabEntire : Boolean = false) : List<CGAPIEventItem> {

        val timestamp = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()
        val dateToday = timestamp.toLocalDateTime(tz).date

        DateTimeFormat

        var url = "https://community.case.edu/mobile_ws/v17/mobile_events_list?range=0&limit=1000&filter4_contains=OR&timestamp=${timestamp.epochSeconds}&filter8=${dateToday.dayOfMonth} ${dateToday.format(monthShortFormat).toString()} ${dateToday.year}&filter4_notcontains=OR&order=undefined&search_word=&&1726272567036"

        if(grabEntire) {
            url = "https://community.case.edu/mobile_ws/v17/mobile_events_list?range=0&limit=1000&filter4_contains=OR&timestamp=${timestamp.epochSeconds}&filter4_notcontains=OR&order=undefined&search_word=&&1726272567036"
        }
        try {
            val response: HttpResponse =  CGAPI.client.get(url)

            if (response.status.value in 200..299) {
                println("Events API Fetched!")

                val cgAPIItems : List<CGAPIEventItem> = response.body()
                return cgAPIItems;
            } else {
                return emptyList()
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }
}