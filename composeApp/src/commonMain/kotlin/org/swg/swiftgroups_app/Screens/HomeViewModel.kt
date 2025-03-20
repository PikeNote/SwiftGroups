package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import org.swg.swiftgroups_app.DatabaseDriver.provideDbDriver
import org.swg.swiftgroups_app.db.Database

class HomeViewModel : ScreenModel {

    init {
        fetchData()
    }

    var upcomingEvents by mutableStateOf(UpcomingEvents(0, emptyList(), 0))
    var profileData by mutableStateOf(emptyList<ProfileDataItem>())

    private fun fetchData() {
        CoroutineScope (Dispatchers.Default).launch {
            upcomingEvents = CGAPI.grabMyEvents()
            profileData = CGAPI.grabProfileData()
            //CGAPI.fetchEventsData()
            FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache"
        }
    }
}