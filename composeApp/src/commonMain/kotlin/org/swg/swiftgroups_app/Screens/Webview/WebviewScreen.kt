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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.launch
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.CGAPI.convertCookie
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ArrowLeft

class WebviewScreen(val url : String, val text : String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

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

            val state = rememberWebViewState(url)

            LaunchedEffect(state) {
                CGAPI.cookieHeader.forEach {
                    state.cookieManager.setCookie(it.domain ?: "https://community.case.edu", cookie = convertCookie(it))
                }
            }

            WebView(state = state, modifier = Modifier.fillMaxWidth().weight(9f))
        }
    }
}