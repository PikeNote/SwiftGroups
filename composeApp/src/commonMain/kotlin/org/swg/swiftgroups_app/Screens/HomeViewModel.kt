package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.grabProfileData
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEvents

class HomeViewModel : ViewModel() {

    init {
        fetchData()
    }

    var upcomingEvents by mutableStateOf(UpcomingEvents(0, emptyList(), 0))
    var profileData by mutableStateOf(emptyList<ProfileDataItem>())

    fun fetchData() {
        viewModelScope.launch {
            upcomingEvents = CGAPI.grabMyEvents()
            profileData = CGAPI.grabProfileData()
        }
    }
}