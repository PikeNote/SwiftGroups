package org.swg.swiftgroups_app.Screens.DBLoading

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroups_app.Screens.Home

class DatabaseLoadingViewModel(navigator : Navigator) : ScreenModel {
    val logs = mutableStateListOf<String>()
    val database = DBObject.db.swiftdataQueries


    init {
        screenModelScope.launch {
            awaitAll (
                screenModelScope.launch { fetchEvents() },
                screenModelScope.launch { fetchClubs() }
            )
            CGAPI.databaseFetched = true
            navigator.replace(Home)
        }
    }

    suspend fun fetchEvents() {
        logs += "Checking for events..."
        if(!database.eventsEmpty().executeAsOne()) {
            logs += "No events found in database! Fetching events now..."
            CGAPI.fetchEventsData(true)
            logs += "Events fetched!"
        }
    }

    suspend fun fetchClubs() {
        logs += "Checking for clubs..."
        if(!database.clubsEmpty().executeAsOne()) {
            logs += "No clubs found in database! Fetching clubs now..."
            CGAPI.fetchAllGroups()
            logs += "Clubs fetched!"
        }
    }

    suspend fun awaitAll(vararg jobs: Job) {
        jobs.asList().joinAll()
    }
}