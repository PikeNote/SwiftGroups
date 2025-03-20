package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.multiplatform.webview.cookie.Cookie
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebViewState
import kotlinx.coroutines.flow.filter
import org.swg.swiftgroups_app.CGAPI.CGAPI

object Login : Screen {

    private var screenModel : LoginViewModel? = null

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        screenModel = rememberScreenModel { LoginViewModel(navigator) }

        val state = rememberWebViewState("https://www.campusgroups.com/shibboleth/login?idp=cwru")

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(screenModel!!.requireLogin) {
                WebView(state = state, modifier = Modifier.fillMaxSize())
            }
        }

        LaunchedEffect(state) {
            snapshotFlow { state.loadingState }
                .filter { it is LoadingState.Finished && state.lastLoadedUrl?.contains("https://community.case.edu/web_app") == true }
                .collect {
                    storeCookies(state, navigator)
                    navigator.replace(Home)
                }
        }

    }

    private suspend fun storeCookies(state : WebViewState, navigator : Navigator) {

        var cookies = state.cookieManager.getCookies("https://community.case.edu")
        cookies += state.cookieManager.getCookies("https://case.edu")
        //setScreenResult("cookies", test_cookies)
        CGAPI.cookieHeader = generateCookieString(cookies)
        screenModel?.secureVault?.set("cg_cookie",CGAPI.cookieHeader)
        navigator.replace(Home)
    }

    private fun generateCookieString(cookieList : List<Cookie>): String {
        var cookieString = ""

        cookieList.forEach {
            cookieString += "${it.name}=${it.value};"
        }

        return cookieString

    }
}