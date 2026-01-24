package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.swg.swiftgroups_app.Components.Home.EventHome
import org.swg.swiftgroups_app.Components.Home.ProfileBar
import org.swg.swiftgroups_app.Fonts.AppFont
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.Icons.QrCodeScan
import org.swg.swiftgroups_app.Screens.Home.QRScreen
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.swiftgroups_title

object ScreenHome : Screen {

    @Composable
    override fun Content() {
        val userPref : UserSettingsPreferences = koinInject()
        val viewModel: HomeViewModel = rememberScreenModel { HomeViewModel(userPref) }
        val refetchProfile by  CGAPI.refetchProfile
        val profileData by viewModel.profileData.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        if(refetchProfile) {
            CGAPI.refetchProfile.value = false
            viewModel.fetchData()
        }

        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(true)
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal=10.dp)) {
                Image(
                    painter = painterResource(Res.drawable.swiftgroups_title),
                    contentDescription = "SwiftGroups Logo",
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(QrCodeScan, "Personal QR Code", modifier = Modifier.clickable {
                    if(viewModel.userQrCode != null) {
                        navigator.push(QRScreen(viewModel.userQrCode!!.qrcodeNumber))
                    }
                }.align(Alignment.CenterEnd).size(30.dp))
            }
            Spacer(Modifier.height(10.dp))

            profileData.forEach { data ->
                ProfileBar(data).Content()
            }

            Spacer(Modifier.height(10.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .offset(x = (10).dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("My Events", style= AppFont.InterTypography.h3)
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .height(195.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp),
                            shadow = Shadow(
                                radius = 0.dp,
                                color = Color.Black.copy(alpha = 0.2f),
                                offset = DpOffset(1.dp, 3.dp)
                            )
                        )
                        .clip(RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp))
                        .background(Color(0xFFd9d9d9)),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    userScrollEnabled = true
                ) {
                    items(viewModel.upcomingEvents, key = {it.eventId}) {
                        EventHome(it, enableButton = false, isUTC = true).Content()
                    }

                    item {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .offset(x = (10).dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Events Hosted by My Groups", style= AppFont.InterTypography.h3)
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .height(195.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp),
                            shadow = Shadow(
                                radius = 0.dp,
                                color = Color.Black.copy(alpha = 0.2f),
                                offset = DpOffset(1.dp, 3.dp)
                            )
                        )
                        .clip(shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp))
                        .background(Color(0xFFd9d9d9))
                        ,
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    userScrollEnabled = true
                ) {

                    items (viewModel.upcomingGroupEvents, key = {it.eventId}) {
                        EventHome(it, enableButton = false, isUTC = false).Content()
                    }

                    item {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }
}