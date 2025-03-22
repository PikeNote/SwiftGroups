package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEvents

class HomeViewModel : ScreenModel {

    init {
        fetchData()
    }

    var upcomingEvents by mutableStateOf(UpcomingEvents(0, emptyList(), 0))
    var profileData by mutableStateOf(emptyList<ProfileDataItem>())

    private fun fetchData() {
        screenModelScope.launch {
            delay(30)
            profileData = Home.profileDataItem.ifEmpty {
                CGAPI.grabProfileData()
            }
            upcomingEvents = CGAPI.grabMyEvents()
            CGAPI.fetchEventsData()
        }
    }
}