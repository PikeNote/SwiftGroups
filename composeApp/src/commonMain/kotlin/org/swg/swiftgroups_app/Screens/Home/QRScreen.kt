package org.swg.swiftgroups_app.Screens.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.Icons.ArrowLeft
import org.swg.swiftgroups_app.Screens.BottomTabVisibilityManager

class QRScreen (val qrString : String) : Screen {


    @Composable
    override fun Content() {
        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(false)
        }


        Box (modifier = Modifier.fillMaxSize().padding(10.dp)) {
            TextButton(
                onClick = {
                    navigator.pop()
                          },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    backgroundColor = Color.Transparent
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).align(Alignment.TopStart),

            ) {
                Icon(
                    ArrowLeft, "",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Black
                )
            }

            Image(
                modifier = Modifier.height(200.dp).fillMaxWidth().align(Alignment.Center),
                painter = rememberQrCodePainter(qrString),
                contentDescription = "QR code displaying $qrString"
            )
        }
    }
}