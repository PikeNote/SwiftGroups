package org.swg.swiftgroups_app.Screens.Groups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.regular.Calendar
import compose.icons.fontawesomeicons.regular.Clipboard
import org.swg.swiftgroups_app.Components.GroupPage.IconText
import org.swg.swiftgroups_app.Components.Home.Button.HorizontalLogoButton
import org.swg.swiftgroups_app.Components.Home.EventHome
import org.swg.swiftgroups_app.Components.SpinningBar
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ArrowLeft
import org.swg.swiftgroups_app.Icons.Person
import org.swg.swiftgroups_app.Screens.Webview.WebviewScreen

class GroupPage (private val groupID : String) : Screen {

    @Composable
    override fun Content() {
        val viewmodel = rememberScreenModel { GroupPageViewModel(groupID) }
        val navigator =  LocalNavigator.currentOrThrow
        val group = viewmodel.group.value

        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            Box (modifier = Modifier.height(250.dp)) {
                AsyncImage(model=group?.group_logo_url?.let{"https://community.case.edu$it"} ?: "https://placehold.co/200x200?text=-",
                    "", modifier = Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.Crop)

                if(group != null) {
                    AsyncImage(model="https://community.case.edu${group.group_cover_url}",
                        "", modifier = Modifier
                            .size(100.dp)
                            .border(border = BorderStroke(2.dp, Color.Black), shape = CircleShape)
                            .clip(CircleShape)
                            .align(Alignment.BottomCenter)

                        , contentScale = ContentScale.Crop)
                } else {
                    SpinningBar(height = 100.dp, Modifier.align(Alignment.BottomCenter))
                }



                TextButton(
                    onClick = {navigator.pop()},
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    ),
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    Icon(
                        ArrowLeft, "",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                }

            }

            Column (modifier = Modifier.padding(horizontal = 15.dp)) {
                Text(group?.name ?: "---", style = AppFont.InterTypography.h3, modifier = Modifier.align(Alignment.CenterHorizontally))
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally)) {
                    Text(group?.group_type ?:  "---", modifier = Modifier.widthIn(min=80.dp).clip(
                        RoundedCornerShape(10.dp)
                    ).background(Color(0xFFD9D9D9)), style=AppFont.InterTypography.body1, textAlign = TextAlign.Center)

                    group?.group_categories?.forEach {
                        Text(it.name, modifier = Modifier.widthIn(min=80.dp).clip(
                            RoundedCornerShape(10.dp)).background(Color(0xFFD9D9D9)), style=AppFont.InterTypography.body1, textAlign = TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(group?.mission ?: "---", style=AppFont.InterTypography.body1)

            }

            Spacer(modifier = Modifier.height(10.dp))

            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                HorizontalLogoButton(
                    logo = FontAwesomeIcons.Regular.Clipboard,
                    text = "Join Club", onClick = {
                        if(group != null) {
                            navigator.push(WebviewScreen(url = group.join_group_url, text = "Join ${group.name}"))
                        }
                    }, width=300.dp,height=50.dp)
            }

            Spacer(modifier = Modifier.height(10.dp))


            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally)) {
                IconText(text = "${group?.events_count ?: "-"} Events", contentDesc = "Event Count", icon = FontAwesomeIcons.Regular.Calendar).Content()

                IconText(text = "${group?.members_count ?: "-"} Members", contentDesc = "Member Count", icon = Person).Content()

                IconText(text = "${group?.officers_count ?: "-"} Officers", contentDesc = "Officer Count", icon = Person).Content()
            }


            Spacer(modifier = Modifier.height(15.dp))

            Column (modifier = Modifier.fillMaxWidth().offset(x = (10).dp)) {
                Text("Events Hosted By This Group", style = AppFont.InterTypography.h3)
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
                    if(group != null) {
                        viewmodel.upcomingEvents.value.forEach { data ->
                            item {
                                EventHome(data).Content()
                            }
                        }
                    }
                }
            }
        }
    }
}