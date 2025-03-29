package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.swg.swiftgroups_app.Components.Home.EventHome
import org.swg.swiftgroups_app.Components.Home.ProfileBar
import org.swg.swiftgroups_app.Fonts.AppFont
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.model.rememberScreenModel

object ScreenHome : Screen {

    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = rememberScreenModel { HomeViewModel() }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SwiftGroups", style=AppFont.InterTypography.h2, fontWeight = FontWeight.Black)

            Spacer(Modifier.height(10.dp))

            viewModel.profileData.forEach { data ->
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
                        .clip(shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp))
                        .background(Color(0xFFd9d9d9)),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    userScrollEnabled = true
                ) {
                    viewModel.upcomingEvents.forEach { data ->
                        item {
                            EventHome(data, enableButton = true).Content()
                        }
                    }
                }
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
                        .clip(shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp))
                        .background(Color(0xFFd9d9d9)),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    userScrollEnabled = true
                ) {

                    viewModel.upcomingGroupEvents.forEach { data ->
                        item {
                            EventHome(data, enableButton = true).Content()
                        }
                    }

                }
            }
        }

    }
}