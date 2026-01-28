package org.swg.swiftgroups_app.Screens.Profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.json
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.DataStore.UserSettings
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.DatabaseDriver.DBObject

class MyUserProfileViewModel(userPrefs : UserSettingsPreferences) :ScreenModel {

    val _profileData = MutableStateFlow<ProfileDataItem?>(null)
    val profileData = _profileData.asStateFlow()
    val settings : UserSettings = userPrefs.settingsFlow.value

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val profileCache = DBObject.db.swiftdataQueries
            .fetchModifications("profileData")
            .executeAsOneOrNull()


        screenModelScope.launch {
            val profileDeferred = async {
                // If the data was fetched more than 60 minutes ago, don't fetch it again
                if (profileCache != null && CGAPI.checkDBExpiry(profileCache.changed_at, settings.cacheTimer) && profileCache.value_ != "[]") {
                    println("Defaulting to cached profile")
                    json.decodeFromString(profileCache.value_)
                } else {
                    CGAPI.fetchMyProfileData()
                }

            }

            _profileData.update { profileDeferred.await().firstOrNull() }
        }
    }
}