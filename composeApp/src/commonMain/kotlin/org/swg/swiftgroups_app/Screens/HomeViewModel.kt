package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
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

class HomeViewModel : ScreenModel {

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
            val profileCache = DBObject.db.swiftdataQueries
                .fetchModifications("profileData")
                .executeAsOneOrNull()

            val myGroupsCache = DBObject.db.swiftdataQueries
                .fetchModifications("homeMyGroups")
                .executeAsOneOrNull()

            val eventsCache = DBObject.db.swiftdataQueries
                .fetchModifications("events")
                .executeAsOneOrNull()

            val clubsCache = DBObject.db.swiftdataQueries
                .fetchModifications("clubs")
                .executeAsOneOrNull()

            val profileDeferred = async {
                // If the data was fetched more than 60 minutes ago, don't fetch it again
                if (profileCache != null && CGAPI.checkDBExpiry(profileCache.changed_at) && profileCache.value_ != "[]") {
                    println("Defaulting to cached profile")
                    json.decodeFromString(profileCache.value_)

                } else {
                    CGAPI.grabProfileData()
                }

            }

            val eventsDeferred = async {
                CGAPI.grabMyEvents().list.map {
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
                        eventTags = "",
                        userCacheData = ""
                    )
                }
            }

            val groupsDeferred = async {
                val groupData = if (myGroupsCache != null && !CGAPI.checkDBExpiry(myGroupsCache.changed_at)
                ) {
                    println("Defaulting to cached my groups")
                    json.decodeFromString<List<ProfileGroupItem>>(myGroupsCache.value_)
                } else {
                    runCatching { CGAPI.fetchMyGroups() }.getOrElse { emptyList() }
                }

                groupData.getOrNull(1)
                    ?.groups
                    ?.flatMap { DBObject.db.swiftdataQueries.fetchEventClub(it.groupName).executeAsList() }
                    .orEmpty()
            }


            val qrDeferred = async {
                CGAPI.fetchProfileQR()
            }

            _profileData.update { profileDeferred.await() }
            upcomingEvents = eventsDeferred.await()
            userQrCode = qrDeferred.await()
            upcomingGroupEvents = groupsDeferred.await()

            if (!CGAPI.databaseFetched) {
                val fetchEventsDeferred = async {
                    if (eventsCache != null && !CGAPI.checkDBExpiry(eventsCache.changed_at)) {
                        println("Defaulting to cached events")
                    } else {
                        CGAPI.fetchEventsData()
                    }
                }

                val fetchGroupsDeferred = async {
                    if (clubsCache != null && !CGAPI.checkDBExpiry(clubsCache.changed_at)) {
                        println("Defaulting to cached clubs")
                    } else {
                        CGAPI.fetchAllPersonalGroups()
                        yield()
                        CGAPI.fetchAllGroups()
                        yield()
                    }
                }

                fetchEventsDeferred.await()
                fetchGroupsDeferred.await()
            }
        }
    }
}
