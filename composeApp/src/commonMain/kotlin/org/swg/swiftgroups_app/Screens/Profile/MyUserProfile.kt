package org.swg.swiftgroups_app.Screens.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.CGAPI.Profile.Job
import org.swg.swiftgroups_app.CGAPI.Profile.Language
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.Components.SpinningBar
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ArrowLeft
import org.swg.swiftgroups_app.Icons.MapPin
import org.swg.swiftgroups_app.Icons.PencilSquare
import org.swg.swiftgroups_app.Screens.BottomTabVisibilityManager
import org.swg.swiftgroups_app.Screens.Webview.WebviewScreen

class MyUserProfile : Screen {

    @Composable
    override fun Content() {
        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        val userPrefs : UserSettingsPreferences = koinInject()

        val viewmodel = rememberScreenModel { MyUserProfileViewModel(userPrefs) }
        val profileDataItem: ProfileDataItem? by viewmodel.profileData.collectAsState()
        val profile = profileDataItem

        LaunchedEffect(Unit) { bottomTabVisibilityManager.setBottomBarVisibility(false) }

        val navigator = LocalNavigator.currentOrThrow

        val contentPadding = Modifier.padding(horizontal = 20.dp)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(bottom = 3.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                            shadow = Shadow(
                                radius = 0.dp,
                                color = Color.Black.copy(alpha = 0.2f),
                                offset = DpOffset(1.dp, 3.dp)
                            )
                        )
                        .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                        .background(
                            Brush.verticalGradient(colorStops = AppTheme.profilePageColorStop)
                        )
                        .padding(horizontal = 20.dp)
                        .statusBarsPadding()
                        ,
            ) {
                Icon(
                    ArrowLeft,
                    "Back Arrow",
                    modifier = Modifier.size(30.dp).clickable { navigator.pop() }.offset(x = 10.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AsyncImage(
                        model =
                            profile
                                ?.photoUrl
                                ?.takeIf { it.isNotBlank() }
                                ?.let { "https://community.case.edu$it" }
                                ?: "https://placehold.co/200x200?text=-",
                        "",
                        modifier = Modifier.size(85.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                "${profile?.firstName.orEmpty()} ${profile?.lastName.orEmpty()}"
                                    .ifBlank { "---" },
                                style = AppFont.InterTypography.headlineMedium
                            )
                            Text(
                                profile?.genderPronoun ?: "---",
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        Text(profile?.accountTypeValue ?: "---")
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(MapPin, "")
                            Text(
                                "${profile?.currentCity ?: "---"}, ${profile?.currentCountry ?: "---"}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (profile == null) {

                SpinningBar(height = 100.dp, Modifier.fillMaxWidth())
            }

            if (profile?.editProfileUrl != null) {

                Button(
                    onClick = {
                        navigator.push(WebviewScreen(profile.editProfileUrl, "Edit Profile"))
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(PencilSquare, "Edit Profile")
                            Text("Edit Profile")
                        }
                    }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().then(contentPadding),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text("Biography", style = AppFont.InterTypography.headlineMedium)
                Text(profile?.bio ?: "---")
            }

            if (profile?.interests?.isNotEmpty() == true) {

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Interests", style = AppFont.InterTypography.headlineMedium)
                    Spacer(modifier = Modifier.height(5.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(profile.interests) { ChipItem(it.interest) }
                    }
                }
            }

            if (profile?.languages?.isNotEmpty() == true) {

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Languages", style = AppFont.InterTypography.headlineMedium)
                    Spacer(modifier = Modifier.height(5.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(profile.languages) { LanguageItem(it) }
                    }
                }
            }

            if (profile?.nationalities?.isNotEmpty() == true) {

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Nationalities", style = AppFont.InterTypography.headlineMedium)
                    Spacer(modifier = Modifier.height(5.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(profile.nationalities) { ChipItem(it.nationality) }
                    }
                }
            }

            if (profile?.jobs?.isNotEmpty() == true) {

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Text("Jobs", style = AppFont.InterTypography.headlineMedium)
                    Spacer(modifier = Modifier.height(5.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(profile.jobs) { JobCard(it) }
                    }
                }

                Spacer(modifier = Modifier.height(50.dp)) // Bottom padding
            }
        }
    }

    @Composable
    fun ChipItem(text: String) {
        Box(
            Modifier.widthIn(min = 70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFD9D9D9))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }

    @Composable
    fun JobCard(job: Job) {
        Column(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFD9D9D9))
                .padding(horizontal = 5.dp)
                .height(70.dp)
                .padding(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(job.company, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(job.industry, fontStyle = FontStyle.Italic, maxLines = 1)
            }

            Text(job.title, maxLines = 1)

            Text(job.period, maxLines = 1)
        }
    }

    @Composable
    fun LanguageItem(lang: Language) {
        Row(
            Modifier.widthIn(min = 120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFD9D9D9))
                .padding(horizontal = 5.dp)
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = "https://community.case.edu${lang.iconUrl}",
                "${lang.language} Flag",
                modifier = Modifier.width(28.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                lang.language,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
