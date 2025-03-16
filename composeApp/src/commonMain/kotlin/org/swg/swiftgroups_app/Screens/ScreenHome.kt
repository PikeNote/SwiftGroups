package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.runBlocking
import org.swg.swiftgroups_app.Components.Home.EventHome
import org.swg.swiftgroups_app.Components.Home.ProfileBar
import org.swg.swiftgroups_app.Fonts.AppFont
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.model.rememberScreenModel

object ScreenHome : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = rememberScreenModel { HomeViewModel() }
        val navigator = LocalNavigator.currentOrThrow


        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Text("SwiftGroups", style=AppFont.InterTypography().h2, fontWeight = FontWeight.Black)

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
                Text("My Events", style= AppFont.InterTypography().h3)
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .height(195.dp)
                        .clip(shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp))
                        .background(Color(0xFFd9d9d9)),
                    contentPadding = PaddingValues(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = true
                ) {
                    runBlocking  {
                        viewModel.upcomingEvents.list.forEach { data ->
                            item {
                                EventHome(data).Content();
                            }
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
                Text("Events Hosted by My Groups", style= AppFont.InterTypography().h3)
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .height(195.dp)
                        .clip(shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp))
                        .background(Color(0xFFd9d9d9)),
                    contentPadding = PaddingValues(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = true
                ) {


                }
            }
        }

    }
}