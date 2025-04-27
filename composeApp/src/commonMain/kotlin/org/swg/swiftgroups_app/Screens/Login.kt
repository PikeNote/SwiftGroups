package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.convertCookie
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroups_app.Screens.DBLoading.DatabaseLoading

object Login : Screen {

    private var screenModel : LoginViewModel? = null

    @Composable
    override fun Content() {


        val navigator = LocalNavigator.currentOrThrow

        screenModel = rememberScreenModel { LoginViewModel(navigator) }

        val state = rememberWebViewState("https://community.case.edu/mobile_ws/v18/mobile_sso_redirect?redirect=https%3A%2F%2Fwww.campusgroups.com%2Fshibboleth%2Flogin%3Fidp%3Dcwru%26ts%3D4501620")
        val webviewNav = rememberWebViewNavigator(
            /*
            requestInterceptor = object : RequestInterceptor {
                override fun onInterceptUrlRequest(
                    request: WebRequest,
                    navigator: WebViewNavigator,
                ): WebRequestInterceptResult {
                    // always clone the incoming request, but add our header
                    val newHeaders = request.headers.toMutableMap().apply {
                        put("X-Requested-With", "com.campusgroups.cwru3")
                    }
                    return WebRequestInterceptResult.Modify(
                        WebRequest(
                            url     = request.url,
                            headers = newHeaders
                        )
                    )
                }
            }
            */
        )

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(screenModel!!.requireLogin) {
                WebView(state = state, modifier = Modifier.fillMaxSize(), navigator = webviewNav)
            }
        }

        LaunchedEffect(Unit) {
            CGAPI.cookieHeader.forEach {
                state.cookieManager.setCookie(it.domain ?: "https://community.case.edu", cookie = convertCookie(it))
            }
        }

        LaunchedEffect(state) {
            snapshotFlow { state.loadingState  }
                .filter { it is LoadingState.Finished && (
                        state.lastLoadedUrl?.contains("https://community.case.edu/web_app") == true ||
                        state.lastLoadedUrl?.contains("https://www.campusgroups.com/shibboleth/login") == true
                        )
                }

                .collect {
                    val url = state.lastLoadedUrl
                    println("Loaded: $url")
                    when {
                        url!!.contains("https://www.campusgroups.com/shibboleth/login") -> {
                            webviewNav.evaluateJavaScript("""
                                (function() {
                                    const loginForm = document.getElementById('login');
                                    if (loginForm) {
                                        loginForm.submit = function() {
                                            console.log("Blocked programmatic form.submit()!");
                                        };
                                    }
                                })();
                            """.trimIndent())
                            delay(100)
                            webviewNav.evaluateJavaScript("""
                                document.querySelector('.loading-message')?.href;
                            """.trimIndent()) { loginUrlResult ->
                                val loginUrl =
                                    loginUrlResult.removeSurrounding("\"") // remove quotes
                                if (loginUrl != "null") {
                                    launch {
                                        val cookieString = state.cookieManager
                                            .getCookies("https://community.case.edu")
                                            .joinToString("; ") { "${it.name}=${it.value}" }
                                        val cgappUrl = CGAPI.fetchAppRedirect(loginUrl, cookieString)
                                        if (cgappUrl != null) {
                                            webviewNav.loadUrl(
                                                cgappUrl
                                            )
                                        }
                                    }


                                } else {
                                    println("Login link not found yet")
                                }
                            }
                        }
                    }
                }
            }

        LaunchedEffect(state) {
            snapshotFlow { state.lastLoadedUrl }
                .filter { url ->
                    url != null && (url.startsWith("cgapp://") || url.startsWith("novalsys-cwru://"))
                }
                .collect { url ->
                    println("CGAPP URL Detected!\n${url}")
                    delay(100)
                    val accessToken = extractAccessToken(url!!)
                    storeCookies(state)

                    if (!accessToken.isNullOrEmpty()) {
                        val tokenSuccess = CGAPI.refreshToken(accessToken)
                        if (tokenSuccess) {
                            if(DBObject.db.swiftdataQueries.eventsEmpty().executeAsOne()) {
                                navigator.replace(TabNavigation)
                            } else {
                                navigator.replace(DatabaseLoading)
                            }
                        } else {
                            println("Token refresh failed! Access token: ${accessToken}")
                        }
                    } else {
                        println("Token seems to be empty? ${url}")
                    }
                }
        }

    }

    private suspend fun storeCookies(state : WebViewState) {

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
        CGAPI.secureVault.set("cg_cookie", cookieData)
    }

    fun extractAccessToken(url: String): String? {
        val query = url.substringAfter("?", "")
        return query.split("&")
            .mapNotNull {
                val (key, value) = it.split("=")
                if (key == "access_token") value else null
            }
            .firstOrNull()
    }


}
