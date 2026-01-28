package org.swg.swiftgroups_app.DateTimeFormats

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

object DateTimeFormat {
    val home_event_date_format = LocalDateTime.Format {
        dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
        chars(", ")
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        day(padding = Padding.ZERO)
    }

    val home_event_time_format = LocalDateTime.Format {
        amPmHour(Padding.NONE)
        char(':')
        minute(Padding.ZERO)
        char(' ')
        amPmMarker(am = "AM", pm = "PM")
    }

    val db_currentTimestamp: DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
        //YYYY-MM-DD HH:MM:SS
        year()
        char('-')
        monthNumber(padding = Padding.ZERO)
        char('-')
        day(padding = Padding.ZERO)
        char(' ')
        hour(padding = Padding.ZERO)
        char(':')
        minute(padding = Padding.ZERO)
        char(':')
        second(padding = Padding.ZERO)
    }

    val ticketDate = LocalDate.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        day()
        char(',')
        char(' ')
        year()
    }

    val ticketTime = LocalTime.Format {
        amPmHour(padding = Padding.NONE)
        char(':')
        minute(padding = Padding.NONE)
        char(' ')
        amPmMarker("AM", "PM")
    }
}