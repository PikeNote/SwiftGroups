package org.swg.swiftgroups_app.Components.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Info
import compose.icons.fontawesomeicons.solid.LocationArrow
import compose.icons.fontawesomeicons.solid.PencilAlt
import compose.icons.fontawesomeicons.solid.Qrcode
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import org.swg.swiftgroups_app.Components.Home.Button.VerticalLogoButton
import org.swg.swiftgroups_app.DateTimeFormats.DateTimeFormat.home_event_date_format
import org.swg.swiftgroups_app.DateTimeFormats.DateTimeFormat.home_event_time_format
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Screens.Event.SingleEventScreen
import org.swg.swiftgroups_app.ShareManager.openMapLocationQuery
import org.swg.swiftgroupsapp.db.Events

class EventHome(
    private val eventData: Events,
    private val cardWidth: Dp = 270.dp,
    private val horizontalPadding: Dp = 0.dp,
    private val enableButton: Boolean = false
) {

    private val currentTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
    private val eventStartTime = Instant.parse(eventData.start_time).toLocalDateTime(TimeZone.UTC)
    private val eventEndTime = Instant.parse(eventData.end_time).toLocalDateTime(TimeZone.UTC)

    @Composable
    fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        Row(
            modifier = Modifier
                .width(cardWidth)
                .padding(horizontal = horizontalPadding)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(15.dp),
                    spotColor = Color.Black.copy(alpha = 0.35f),
                    ambientColor = Color.Black.copy(alpha = 0.35f)
                )
                .height(174.dp)
                .clip(shape = RoundedCornerShape(15.dp))
                .background(Color(0xFFf2f1f1))
                .clickable {
                    navigator.push(SingleEventScreen(eventData.eventId.toInt()))
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(15.dp)
                    )
            ) {
                Box(
                    modifier = Modifier.then(
                        if (currentTime >= eventStartTime && currentTime <= eventEndTime) {
                            Modifier.border(
                                width = 2.dp,
                                color = Color.Red,
                                shape = RoundedCornerShape(15.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = eventData.eventPicture,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth()
                            )
                            if (currentTime >= eventStartTime && currentTime <= eventEndTime) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(70.dp)
                                            .height(24.dp)
                                            .background(
                                                color = Color(0xFFFF0000),
                                                shape = RoundedCornerShape(
                                                    topStart = 0.dp,
                                                    topEnd = 15.dp,
                                                    bottomStart = 20.dp,
                                                    bottomEnd = 0.dp
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "LIVE",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = AppFont.InterTypography.body2
                                        )
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(eventData.eventName, fontWeight = FontWeight.Bold,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1)
                            }
                            Text(
                                "${eventStartTime.format(home_event_date_format)} | ${eventStartTime.format(home_event_time_format)} - ${eventEndTime.format(
                                    home_event_time_format)}",
                                style = AppFont.InterTypography.body2,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .clickable(onClick = {}),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    FontAwesomeIcons.Solid.LocationArrow, "Navigation Arrow",
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    eventData.eventLocation,
                                    color = Color.Blue,
                                    style = AppFont.InterTypography.subtitle1,
                                    modifier = Modifier.clickable {
                                        openMapLocationQuery(eventData.eventLocation)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if(enableButton) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .background(Color(0xFFf2f1f1))
                        .fillMaxHeight()
                        .width(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                        VerticalLogoButton(
                            modifier = Modifier.weight(1f),
                            logo = FontAwesomeIcons.Solid.Info,
                            text = "Info",
                            onClick = {
                                navigator.push(SingleEventScreen(eventData.eventId.toInt()))
                            }
                        )
                        VerticalLogoButton(
                            modifier = Modifier.weight(1f),
                            logo = FontAwesomeIcons.Solid.PencilAlt,
                            text = "Edit",
                            onClick = {}
                        )
                        VerticalLogoButton(
                            modifier = Modifier.weight(1f),
                            logo = FontAwesomeIcons.Solid.Qrcode,
                            text = "QR Code",
                            onClick = {}
                        )
                    }
                }
        }
    }
}