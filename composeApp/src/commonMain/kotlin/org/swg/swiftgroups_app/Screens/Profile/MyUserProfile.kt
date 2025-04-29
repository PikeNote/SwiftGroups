package org.swg.swiftgroups_app.Screens.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.Components.SpinningBar
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ArrowLeft
import org.swg.swiftgroups_app.Icons.MapPin
import org.swg.swiftgroups_app.Icons.PencilSquare
import org.swg.swiftgroups_app.Screens.BottomTabVisibilityManager
import org.swg.swiftgroups_app.Screens.Webview.WebviewScreen

class MyUserProfile (private val initProfileDataItem: ProfileDataItem?) : Screen {

    @Composable
    override fun Content() {
        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()


        val viewmodel = rememberScreenModel { MyUserProfileViewModel(initProfileDataItem) }
        val profileDataItem : ProfileDataItem? by viewmodel.profileData.collectAsState()
        val profile = profileDataItem

        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(false)
        }

        val navigator = LocalNavigator.currentOrThrow



        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column (modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .shadow(3.dp, shape=RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .padding(bottom = 3.dp)
                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .background(Brush.verticalGradient(colorStops = AppTheme.profilePageColorStop))
                .padding(horizontal = 20.dp)
                .statusBarsPadding(),

            ) {
                Icon(
                    ArrowLeft, "Back Arrow",
                    modifier = Modifier
                        .size(30.dp).clickable {
                            navigator.pop()
                        }.offset(x = 10.dp),
                    tint = Color.White

                )
                Spacer(modifier = Modifier.height(3.dp))
                Row (horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    AsyncImage(model= profile?.photoUrl
                        ?.takeIf { it.isNotBlank() }
                        ?.let { "https://community.case.edu$it" }
                        ?: "https://placehold.co/200x200?text=-",  ""
                        , modifier = Modifier.size(85.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("${profile?.firstName.orEmpty()} ${profile?.lastName.orEmpty()}".ifBlank { "---" }, style = AppFont.InterTypography.h4)
                            Text(profile?.genderPronoun ?: "---", fontStyle = FontStyle.Italic, modifier = Modifier.align(Alignment.CenterVertically))
                        }
                        Text(profile?.accountTypeValue ?: "---")
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(MapPin,  "")
                            Text("${profile?.currentCity ?: "---"}, ${profile?.currentCountry ?: "---"}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if(profile == null) {
                SpinningBar(height = 100.dp, Modifier.align(Alignment.CenterHorizontally))
            }

            if(profile?.editProfileUrl != null) {
                Button(onClick = {
                    navigator.push(WebviewScreen(profile.editProfileUrl, "Edit Profile" ))
                }, modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), content = {
                    Row (horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(PencilSquare, "Edit Profile")
                        Text("Edit Profile")
                    }
                })
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(horizontal = 20.dp)) {
                Column (verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Biography", style = AppFont.InterTypography.h4)
                    Text(profile?.bio ?: "---")
                }

                if(profile != null) {
                    if(profile.interests.isNotEmpty()) {
                        Column (verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Interests", style = AppFont.InterTypography.h4)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                                profile.interests.forEach {
                                    item {
                                        Box (Modifier.widthIn(min = 70.dp).clip(
                                            RoundedCornerShape(8.dp)
                                        ).background(Color(0xFFD9D9D9)).padding(horizontal = 5.dp).height(28.dp), contentAlignment = Alignment.Center) {
                                            Text(
                                                it.interest,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center, maxLines = 1
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }

                    if(profile.sports.isNotEmpty()) {
                        Column (verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Sports", style = AppFont.InterTypography.h4)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                                profile.sports.forEach {
                                    if(it != null) {
                                        item {
                                            Box(
                                                Modifier.widthIn(min = 70.dp).clip(
                                                    RoundedCornerShape(8.dp)
                                                ).background(Color(0xFFD9D9D9)).padding(horizontal = 5.dp)
                                                    .height(28.dp), contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    it,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center, maxLines = 1
                                                )
                                            }

                                        }
                                    }

                                }
                            }
                        }
                    }


                    if(profile.languages.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Languages", style = AppFont.InterTypography.h4)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                profile.languages.forEach {
                                    item {
                                        Row(
                                            Modifier.widthIn(min = 120.dp).clip(
                                                RoundedCornerShape(8.dp)
                                            ).background(Color(0xFFD9D9D9)).padding(horizontal = 5.dp)
                                                .height(28.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            AsyncImage(
                                                model = "https://community.case.edu${it.iconUrl}",
                                                "${it.language} Flag",
                                                modifier = Modifier.width(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                it.language,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center, maxLines = 1
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }

                    if(profile.nationalities.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Nationalities", style = AppFont.InterTypography.h4)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                profile.nationalities.forEach {
                                    item {
                                        Box(
                                            Modifier.widthIn(min = 70.dp).clip(
                                                RoundedCornerShape(8.dp)
                                            ).background(Color(0xFFD9D9D9)).padding(horizontal = 5.dp)
                                                .height(28.dp), contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                it.nationality,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center, maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(profile.jobs.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Jobs", style = AppFont.InterTypography.h4)
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                profile.jobs.forEach {
                                    item {
                                        Column(
                                            Modifier.fillMaxWidth().clip(
                                                RoundedCornerShape(8.dp)
                                            ).background(Color(0xFFD9D9D9)).padding(horizontal = 5.dp)
                                                .height(70.dp).padding(10.dp)
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                                Text(
                                                    it.company,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    it.industry,
                                                    fontStyle = FontStyle.Italic,
                                                    maxLines = 1
                                                )
                                            }

                                            Text(
                                                it.title,
                                                maxLines = 1
                                            )

                                            Text(
                                                it.period,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }
        }
    }
}