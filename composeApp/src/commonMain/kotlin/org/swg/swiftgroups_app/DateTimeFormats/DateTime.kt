package org.swg.swiftgroups_app.DateTimeFormats

import kotlinx.datetime.LocalDateTime
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
        dayOfMonth()
    }

    val home_event_time_format = LocalDateTime.Format {
        amPmHour(Padding.NONE)
        char(' ')
        minute(Padding.ZERO)
        char(' ')
        amPmMarker(am = "AM", pm = "PM")
    }
}