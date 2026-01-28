package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.runBlocking
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroups_app.Screens.DBLoading.DatabaseLoading

class LoginViewModel (navigator : Navigator?) : ScreenModel {

    var requireLogin by mutableStateOf(false)
    var navigator by mutableStateOf(false)

    init {
        try {
            val authKey = CGAPI.secureVault.getString("authKey", null)
            if (!authKey.isNullOrEmpty()) {
                runBlocking {
                    val auth = CGAPI.refreshToken(authKey)
                    if (auth) {
                        if(DBObject.db.swiftdataQueries.eventsEmpty().executeAsOne())  {
                            navigator?.replace(TabNavigation)
                        } else {
                            navigator?.replace(DatabaseLoading)
                        }

                    } else {
                        requireLogin = true
                    }
                }
            } else {
                requireLogin = true
            }
        } catch (e: Exception) {
            // If there's any other error, require login
            requireLogin = true
        }
    }


}