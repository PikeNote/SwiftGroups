package org.swg.swiftgroups_app.Screens.Event


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.EventAPI.EventSpecificAPI


class SingleEventViewModel ( val eventID : Int) : ScreenModel {

    var eventSpecificAPI : MutableState<EventSpecificAPI?> = mutableStateOf(null)

    init {
        screenModelScope.launch {
            eventSpecificAPI.value = CGAPI.fetchEvent(eventID.toString())
        }
    }
}