package org.swg.swiftgroups_app.Screens.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.Components.CustomSwitch
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.BootstrapArchive
import org.swg.swiftgroups_app.Icons.Database
import org.swg.swiftgroups_app.Icons.MaterialSymbolsAuto_timer
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.swiftgroups_title

object SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val userPrefs : UserSettingsPreferences = koinInject()
        val viewModel: SettingsViewModel = rememberScreenModel { SettingsViewModel(userPrefs) }
        val state by viewModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Image(
                painter = painterResource(Res.drawable.swiftgroups_title),
                contentDescription = "SwiftGroups Logo",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Row (horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Database and Storage",
                    style = AppFont.InterTypography.headlineMedium)
                Icon(
                    Database,  "Database Icon",
                    modifier = Modifier.size(24.dp))
            }
            Row {
                Column (Modifier.weight(1f)) {
                    Text("Event Database",
                        style = AppFont.InterTypography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${viewModel.eventCount.value} Events Stored",
                        style = AppFont.InterTypography.bodyLarge)
                    Text("Last Fetched: ${viewModel.eventLastModified.value}",
                        style = AppFont.InterTypography.bodyLarge)

                }
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    SettingsButton("Force Fetch",{}, Color(0xFF6D7FB5))
                    SettingsButton("Wipe Database",{}, Color(0xFFFF3B30))
                }

            }
            Spacer(modifier = Modifier.height(5.dp))
            Row () {
                Column (Modifier.weight(1f)) {
                    Text("Club Database",
                        style = AppFont.InterTypography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${viewModel.clubCount.value} Clubs Stored",
                        style = AppFont.InterTypography.bodyLarge)
                    Text("Last Fetched: ${viewModel.clubLastModified.value}",
                        style = AppFont.InterTypography.bodyLarge)

                }
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    SettingsButton("Force Fetch",{}, Color(0xFF6D7FB5))
                    SettingsButton("Wipe Database",{}, Color(0xFFFF3B30))
                }

            }

            Row (horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Cache Settings",
                    style = AppFont.InterTypography.headlineMedium)
                Icon(
                    BootstrapArchive,  "Database Icon",
                    modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(5.dp))


            Column () {
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Cache Events",
                        style = AppFont.InterTypography.titleMedium, fontWeight = FontWeight.Bold)

                    CustomSwitch(
                        checked = state.cacheEvents,
                        onCheckedChange = {
                            viewModel.onEventToggle(it)
                        }
                    )
                }
                Text("Cache the event details on first load to display on subsequent loads while up to date information is fetched in the background",
                    style = AppFont.InterTypography.bodyLarge)

            }

            Spacer(modifier = Modifier.height(5.dp))


            Column () {
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Cache Clubs",
                        style = AppFont.InterTypography.titleMedium, fontWeight = FontWeight.Bold)
                    CustomSwitch(
                        checked = state.cacheClubs,
                        onCheckedChange = {
                            viewModel.onClubToggle(it)
                        }
                    )
                }
                Text("Cache the group information on first load to display on subsequent loads while up to date information is fetched in the background",
                    style = AppFont.InterTypography.bodyLarge)

            }

            Spacer(modifier = Modifier.height(10.dp))

            Row (horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Cache Timer",
                    style = AppFont.InterTypography.titleLarge)
                Icon(
                    MaterialSymbolsAuto_timer,  "Database Icon",
                    modifier = Modifier.size(24.dp))
            }

            Slider(
                value = state.cacheTimer.toFloat(),
                onValueChange = { viewModel.onTimerChange(it.toInt()) },
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF6D7FB5),
                    activeTrackColor = Color(0xFF6D7FB5),
                    inactiveTrackColor = Color(0xFFc5cce1),
                    inactiveTickColor = Color.White,
                    activeTickColor = Color.White
                ),
                steps = 9,
                valueRange = 0f..10f
            )
            Text(text = "${state.cacheTimer} Minutes")

            Spacer(modifier = Modifier.height(5.dp))

            Text("Select how old must the cached data be until you want the app to fetch updated events/club information",
                style = AppFont.InterTypography.bodyLarge)

        }
    }

    @Composable
    fun SettingsButton(buttonText: String, onClick: () -> Unit, backgroundColor: Color) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(25.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.width(130.dp).height(25.dp)
        ) {
                Text(text = buttonText,
                    style = AppFont.InterTypography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
        }
    }
}