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
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebViewState
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.convertCookie

object Login : Screen {

    private var screenModel : LoginViewModel? = null

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        screenModel = rememberScreenModel { LoginViewModel(navigator) }

        val state = rememberWebViewState("https://www.campusgroups.com/shibboleth/login?idp=cwru")


        runBlocking {
            CGAPI.cookieHeader.forEach {
                state.cookieManager.setCookie(it.domain ?: "https://community.case.edu", cookie = convertCookie(it))
            }

        }

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
                    delay(300)
                    storeCookies(state, navigator)
                    navigator.replace(Home)
                }
        }

    }

    private suspend fun storeCookies(state : WebViewState, navigator : Navigator) {

        val cookies = state.cookieManager.getCookies("https://community.case.edu").toMutableList()
        cookies += state.cookieManager.getCookies("https://case.edu")
        cookies += state.cookieManager.getCookies("https://campusgroups.com")

        val ktorCookies : MutableList<io.ktor.http.Cookie> = mutableListOf()

        cookies.forEach {
            ktorCookies.add(io.ktor.http.Cookie(
                name = it.name,
                value = it.value,
                expires = GMTDate(it.expiresDate),
                maxAge = it.maxAge?.toInt(),
                domain = it.domain,
                path = it.path,
            ))
        }
        val cookieData : String = Json.encodeToString(ktorCookies)



        CGAPI.cookieHeader = ktorCookies
        screenModel?.secureVault?.set("cg_cookie", cookieData)
        navigator.replace(Home)
    }
}