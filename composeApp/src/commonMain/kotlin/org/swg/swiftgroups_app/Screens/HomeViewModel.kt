package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.Groups.GroupItem
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEvents
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Events

class HomeViewModel : ScreenModel {

    init {
        fetchData()
    }

    var upcomingEvents by mutableStateOf(emptyList<Events>())
    var profileData by mutableStateOf(emptyList<ProfileDataItem>())
    var upcomingGroupEvents by mutableStateOf(emptyList<Events>())

    private fun fetchData() {
        screenModelScope.launch {
            delay(30)
            profileData = Home.profileDataItem.ifEmpty {
                CGAPI.grabProfileData()
            }

            val upcomingEventStaging : MutableList<Events> = mutableListOf()
            val upcomingEventsData = CGAPI.grabMyEvents()

            upcomingEventsData.list.forEach {
                upcomingEventStaging += Events(
                    eventId = it.event_id.toLong(),
                    eventName = it.event_name,
                    start_time = it.event_start_utc,
                    end_time = it.event_end_utc,
                    eventDesc = "",
                    eventAttendees = it.attendees_count.toLong(),
                    eventUrl = "",
                    clubURL = "",
                    eventLocation = it.location,
                    eventPicture = "https://community.case.edu${it.photo_url}" ?: "",
                    clubName = it.event_group,
                    eventCategory = ""
                )
            }

            upcomingEvents = upcomingEventStaging;

            val groupData = CGAPI.fetchGroups();

            if(groupData.isNotEmpty()) {
                val myGroups = groupData[1];
                val groupEvents : MutableList<Events> = mutableListOf()
                myGroups.groups.forEach {
                    groupEvents += DBObject.db.swiftdataQueries.fetchEventClub(it.groupName).executeAsList()
                }
                upcomingGroupEvents = groupEvents
            }

            CGAPI.fetchEventsData(true)
        }
    }
}