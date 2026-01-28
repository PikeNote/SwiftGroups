package org.swg.swiftgroups_app.DataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.io.IOException
import okio.Path.Companion.toPath

data class UserSettings(
    val cacheEvents: Boolean = false,
    val cacheClubs: Boolean = false,
    val cacheTimer: Int = 0,
    val eventCount: Int = 0,
    val clubCount: Int = 0
)

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )
internal const val dataStoreFileName = "swft.preferences_pb"

class UserSettingsPreferences(private val dataStore: DataStore<Preferences>, private val scope: CoroutineScope) {
    private object Keys {
        val CACHE_EVENTS = booleanPreferencesKey("cache_events")
        val CACHE_CLUBS = booleanPreferencesKey("cache_clubs")
        val CACHE_TIMER = intPreferencesKey("cache_timer")
    }

    val settingsFlow: StateFlow<UserSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
        UserSettings(
            cacheEvents = prefs[Keys.CACHE_EVENTS] ?: true,
            cacheClubs = prefs[Keys.CACHE_CLUBS] ?: true,
            cacheTimer = prefs[Keys.CACHE_TIMER] ?: 5
        )
    }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = UserSettings()
        )

    suspend fun updateCacheEvents(enabled: Boolean) {
        dataStore.edit { it[Keys.CACHE_EVENTS] = enabled }
    }

    suspend fun updateCacheClubs(enabled: Boolean) {
        dataStore.edit { it[Keys.CACHE_CLUBS] = enabled }
    }

    suspend fun updateCacheTimer(value: Int) {
        dataStore.edit { it[Keys.CACHE_TIMER] = value }
    }
}