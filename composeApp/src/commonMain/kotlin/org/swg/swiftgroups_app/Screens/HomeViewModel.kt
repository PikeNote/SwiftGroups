package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Events

class HomeViewModel () : ScreenModel {

    init {
        fetchData()
    }

    var upcomingEvents by mutableStateOf(emptyList<Events>())
    var profileData by mutableStateOf(emptyList<ProfileDataItem>())
    var upcomingGroupEvents by mutableStateOf(emptyList<Events>())

    fun fetchData() {
        screenModelScope.launch {
            delay(30)
            profileData = TabNavigation.profileDataItem.ifEmpty { CGAPI.grabProfileData() }
            yield()
            val upcomingEventStaging: MutableList<Events> = mutableListOf()
            val upcomingEventsData = CGAPI.grabMyEvents()

            upcomingEventsData.list.forEach {
                upcomingEventStaging +=
                    Events(
                        eventId = it.event_id.toLong(),
                        eventName = it.event_name,
                        start_time = it.event_start_utc,
                        end_time = it.event_end_utc,
                        eventDesc = "",
                        eventAttendees = it.attendees_count.toLong(),
                        eventUrl = "",
                        clubURL = "",
                        eventLocation = it.location,
                        eventPicture = "https://community.case.edu${it.photo_url}",
                        clubName = it.event_group,
                        eventCategory = "",
                        userCacheData = ""
                    )
            }

            upcomingEvents = upcomingEventStaging

            yield()

            try {
                val groupData = CGAPI.fetchMyGroups()
                if (groupData.isNotEmpty()) {
                    val myGroups = groupData[1]
                    val groupEvents: MutableList<Events> = mutableListOf()
                    myGroups.groups.forEach {
                        groupEvents +=
                            DBObject.db.swiftdataQueries
                                .fetchEventClub(it.groupName)
                                .executeAsList()
                    }
                    upcomingGroupEvents = groupEvents
                }
            } catch (e: Exception) {
                //
            }
            yield()

            if (!CGAPI.databaseFetched) {
                try {
                    CGAPI.fetchEventsData()
                    CGAPI.fetchAllGroups()
                } catch (e: Exception) {
                    //
                }
            }
        }
    }
}
