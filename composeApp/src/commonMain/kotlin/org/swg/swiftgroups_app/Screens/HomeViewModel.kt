package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents

class HomeViewModel : ViewModel() {
    var upcomingEvents by mutableStateOf(UpcomingEvents(0, emptyList(), 0))

    fun test() {
        viewModelScope.launch {
            upcomingEvents = CGAPI.grabMyEvents()
        }
    }
}