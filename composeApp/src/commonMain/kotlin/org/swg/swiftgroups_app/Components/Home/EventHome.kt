package org.swg.swiftgroups_app.Components.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Info
import compose.icons.fontawesomeicons.solid.LocationArrow
import compose.icons.fontawesomeicons.solid.PencilAlt
import compose.icons.fontawesomeicons.solid.Qrcode
import org.swg.swiftgroups_app.CGAPI.UpcomingEvents.UpcomingEventData
import org.swg.swiftgroups_app.Components.Home.Button.VerticalLogoButton
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Screens.Event.SingleEventScreen

class EventHome (private val eventDat : UpcomingEventData) {




    @Composable
    fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        Row(
            modifier = Modifier
                .width(270.dp)
                .height(174.dp)
                .clip(shape = RoundedCornerShape(15.dp))
                .background(Color(0xFFf2f1f1)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .width(208.dp)
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(15.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .width(205.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(Color.White)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AsyncImage(
                        model = "https://community.case.edu${eventDat.photo_url}",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                    )
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(eventDat.event_date, fontWeight = FontWeight.Bold)
                        Text("${eventDat.event_start_time} - ${eventDat.event_end_time}", style = AppFont.InterTypography.body2, fontWeight = FontWeight.Bold )
                        Row (
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .clickable(onClick = {

                                }),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)

                        ) {
                            Icon(FontAwesomeIcons.Solid.LocationArrow,  "Navigation Arrow",
                                modifier = Modifier
                                    .size(10.dp)

                            )
                            Text(eventDat.location, color = Color.Blue, style = AppFont.InterTypography.subtitle1)
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.background(Color(0xFFf2f1f1))
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                VerticalLogoButton(modifier =
                    Modifier.weight(1f),
                    logo = FontAwesomeIcons.Solid.Info,
                    text = "Info",
                    onClick = {
                        navigator.push(SingleEventScreen(eventDat.event_id))
                    }
                )
                VerticalLogoButton(modifier =
                    Modifier.weight(1f),
                    logo = FontAwesomeIcons.Solid.PencilAlt,
                    text = "Edit",
                    onClick = {

                    }
                )

                VerticalLogoButton(modifier =
                    Modifier.weight(1f),
                    logo = FontAwesomeIcons.Solid.Qrcode,
                    text = "QR Code",
                    onClick = {
                    }
                )
            }
        }
    }
}