package org.swg.swiftgroups_app.Screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.runBlocking
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.SecureStorage.SecureStorage

class LoginViewModel (navigator : Navigator?) : ScreenModel {

    val secureVault = SecureStorage()
    var requireLogin by mutableStateOf(false)
    var navigator by mutableStateOf(false)

    init {
        if(secureVault.existsObject("cg_cookie")) {
            CGAPI.cookieHeader = secureVault.data("cg_cookie").toString();
            runBlocking {
                val loggedIn = CGAPI.checkLoggedIn()
                if(loggedIn) {
                    if (navigator != null) {
                        navigator.replace(Home)
                    }
                } else {
                    requireLogin = true;
                }
            }

        } else {
            requireLogin = true;
        }
    }
}