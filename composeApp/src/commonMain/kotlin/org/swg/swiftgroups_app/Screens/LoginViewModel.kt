package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.SecureStorage.SecureStorage

class LoginViewModel (navigator : Navigator?) : ScreenModel {

    val secureVault = SecureStorage()
    var requireLogin by mutableStateOf(false)
    var navigator by mutableStateOf(false)

    init {
        try {
            val storedCookie = secureVault.getString("cg_cookie", null)
            if (!storedCookie.isNullOrEmpty()) {
                CGAPI.cookieHeader = Json.decodeFromString(storedCookie)
                runBlocking {
                    val loggedIn = CGAPI.checkLoggedIn()
                    if (loggedIn.isNotEmpty()) {
                        Home.profileDataItem = loggedIn
                        navigator?.replace(Home)
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