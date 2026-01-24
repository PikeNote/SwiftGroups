package org.swg.swiftgroups_app.Screens.Settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.swg.swiftgroups_app.DataStore.UserSettings
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroups_app.DateTimeFormats.DateTimeFormat.db_currentTimestamp

class SettingsViewModel(private val userPrefs: UserSettingsPreferences) : ScreenModel {

    val uiState: StateFlow<UserSettings> = userPrefs.settingsFlow
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )



    val dateTimeFormat = LocalDateTime.Format {
        monthNumber(padding = Padding.ZERO)
        char('/')
        day(padding = Padding.ZERO)
        char(' ')
        yearTwoDigits(2000)
        char('/')
        amPmHour(padding = Padding.ZERO)
        char(':')
        minute(padding = Padding.ZERO)
        amPmMarker("AM", "PM")
    }

    val swiftdataQueries = DBObject.db.swiftdataQueries

    private val _eventCount = MutableStateFlow(swiftdataQueries.countEvents().executeAsOneOrNull() ?: 0L)
    val eventCount: StateFlow<Long> = _eventCount.asStateFlow()

    private val _eventLastModified = MutableStateFlow(ISOTimeStampToDateString(swiftdataQueries.fetchModifications("events").executeAsOneOrNull()?.changed_at ?: ""))
    val eventLastModified: StateFlow<String> = _eventLastModified.asStateFlow()

    private val _clubCount = MutableStateFlow(swiftdataQueries.countClubs().executeAsOne())
    val clubCount: StateFlow<Long> = _clubCount.asStateFlow()

    private val _clubLastModified = MutableStateFlow(ISOTimeStampToDateString(swiftdataQueries.fetchModifications("clubs").executeAsOneOrNull()?.changed_at?: ""))
    val clubLastModified: StateFlow<String> = _clubLastModified.asStateFlow()


    fun onClubToggle(isEnabled: Boolean) {
        screenModelScope.launch {
            userPrefs.updateCacheClubs(isEnabled)
        }
    }

    fun onEventToggle(isEnabled: Boolean) {
        screenModelScope.launch {
            userPrefs.updateCacheEvents(isEnabled)
        }
    }

    fun onTimerChange(value: Int) {
        screenModelScope.launch {
            userPrefs.updateCacheTimer(value)
        }
    }

    fun ISOTimeStampToDateString(isoString: String): String {
        if(isoString == "") return "Never"
        val isoTime = LocalDateTime.parse(isoString, db_currentTimestamp).toInstant(TimeZone.UTC)

        return isoTime.toLocalDateTime(TimeZone.currentSystemDefault()).format(dateTimeFormat)
    }
}