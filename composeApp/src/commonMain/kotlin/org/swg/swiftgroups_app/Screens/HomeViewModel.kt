package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.json
import org.swg.swiftgroups_app.CGAPI.Groups.HomeGroup.ProfileGroupItem
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.CGAPI.Profile.UserProfileQRCode
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroupsapp.db.Events

class HomeViewModel () : ScreenModel {

    init {
        fetchData()
    }

    var upcomingEvents by mutableStateOf(emptyList<Events>())

    private val _profileData = MutableStateFlow(emptyList<ProfileDataItem>())
    val profileData: StateFlow<List<ProfileDataItem>> = _profileData.asStateFlow()

    var upcomingGroupEvents by mutableStateOf(emptyList<Events>())
    var userQrCode : UserProfileQRCode? by mutableStateOf(null)

    fun fetchData() {
        screenModelScope.launch(Dispatchers.IO) {
            delay(30)
            val profileCache = DBObject.db.swiftdataQueries.fetchModifications("profileData").executeAsOneOrNull()
            _profileData.update {
                if (profileCache == null) {
                    CGAPI.grabProfileData()
                } else {
                    // If the data was fetched more than 60 minutes ago, don't fetch it again
                    if (CGAPI.checkDBExpiry(profileCache.changed_at) && profileCache.value_ != "[]") {
                        println("Defaulting to cached profile")
                        json.decodeFromString(profileCache.value_)

                    } else {
                        CGAPI.grabProfileData()
                    }
                }
            }

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

            userQrCode = CGAPI.fetchProfileQR()

            yield()


            try {
                val myGroupCache = DBObject.db.swiftdataQueries.fetchModifications("homeMyGroups").executeAsOneOrNull()
                val groupData : List<ProfileGroupItem> = if(myGroupCache == null) {
                    CGAPI.fetchMyGroups()
                } else {
                    try {
                        if (CGAPI.checkDBExpiry(myGroupCache.changed_at)) {
                            println("Defaulting to cached my groups")
                            json.decodeFromString(myGroupCache.value_)
                        } else {
                            CGAPI.fetchMyGroups()
                        }
                    } catch (_:Exception) {CGAPI.fetchMyGroups()}
                }

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
                    val eventsCache = DBObject.db.swiftdataQueries.fetchModifications("events").executeAsOneOrNull()
                    val groupsCache = DBObject.db.swiftdataQueries.fetchModifications("clubs").executeAsOneOrNull()

                    try {
                        if(eventsCache != null) {
                            if(!CGAPI.checkDBExpiry(eventsCache.changed_at)) {
                                CGAPI.fetchEventsData()
                            } else {
                                println("Defaulting to cached events")
                            }
                        }
                    } catch (_:Exception) {
                        CGAPI.fetchEventsData()
                    }
                    yield()

                    try {
                        if (groupsCache != null) {
                            if (!CGAPI.checkDBExpiry(groupsCache.changed_at)) {
                                CGAPI.fetchAllPersonalGroups()
                                yield()
                                CGAPI.fetchAllGroups()
                                yield()
                            } else {
                                println("Defaulting to cached groups")
                            }
                        }
                    } catch (_: Exception) {
                        CGAPI.fetchAllPersonalGroups()
                        yield()
                        CGAPI.fetchAllGroups()
                        yield()
                    }
                } catch (e: Exception) {
                    //
                }
            }
        }
    }
}
