package org.swg.swiftgroups_app.Screens.Webview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.convertCookie
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ArrowLeft

class WebviewScreen(val url : String, val text : String, val callback : ()->Unit = {}, val urlMatch : String = "") : Screen {


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var cookieSetAttempted by remember { mutableStateOf(false) }



        Column (
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            Box (modifier = Modifier
                .weight(1f)
                .background(Brush.horizontalGradient(colorStops = AppTheme.profileColorStops))
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                TextButton(
                    onClick = {navigator.pop()},
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        backgroundColor = Color.Transparent
                    ),
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                        Icon(
                            ArrowLeft, "Back Arrow",
                            modifier = Modifier
                                .size(30.dp),
                            tint = Color.White
                        )
                }
                Text(text, modifier = Modifier.align(Alignment.Center),style=AppFont.InterTypography.h3, color =Color.White)
            }

            val state = rememberWebViewState(url, additionalHttpHeaders = mapOf("Cookie" to CGAPI.generateCookieString(CGAPI.cookieHeader)),)
            val webNavigator = rememberWebViewNavigator()
            val pullToRefreshState = rememberPullToRefreshState()
            var isRefreshing by remember { mutableStateOf(false) }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    webNavigator.reload()
                },
                state = pullToRefreshState,
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = Color.White,
                        color = Color(0xFF1A73E8)
                    )
                },
                modifier = Modifier.weight(9f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    WebView(
                        state = state,
                        navigator = webNavigator,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            LaunchedEffect(state) {
                snapshotFlow { state.loadingState }
                    .distinctUntilChanged()
                    .filter { it is LoadingState.Finished }

                    .collect {
                        isRefreshing = false
                        if(urlMatch.isNotEmpty() && state.lastLoadedUrl?.contains(urlMatch) == true) {
                            callback()
                            CGAPI.refetchProfile.value=true
                            navigator.pop()
                        }

                        if (!cookieSetAttempted) {
                            cookieSetAttempted = true
                            CGAPI.cookieHeader.forEach {
                                state.cookieManager.setCookie(it.domain ?: "https://community.case.edu", cookie = convertCookie(it))
                            }
                        }


                    }
            }
        }
    }
}