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
import compose.icons.fontawesomeicons.solid.LocationArrow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.swg.swiftgroups_app.DateTimeFormats.DateTimeFormat
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Screens.Event.SingleEventScreen
import org.swg.swiftgroups_app.ShareManager.openMapLocationQuery
import org.swg.swiftgroupsapp.db.Events

class EventsCard(
    private val eventDat: Events,
    private val cardWidth: Dp = 270.dp,
    private val horizontalPadding: Dp = 0.dp
) {
    private val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private val eventStartTime = Instant.parse(eventDat.start_time).toLocalDateTime(TimeZone.currentSystemDefault())
    private val eventEndTime = Instant.parse(eventDat.end_time).toLocalDateTime(TimeZone.currentSystemDefault())

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
                .height(200.dp)
                .clip(shape = RoundedCornerShape(15.dp))
                .background(Color(0xFFf2f1f1))
                .then(
                    if (currentTime in eventStartTime..eventEndTime) {
                        Modifier.border(
                            width = 2.dp,
                            color = Color.Red,
                            shape = RoundedCornerShape(15.dp)
                        )
                    } else {
                        Modifier
                    }
                ).clickable {
                    navigator.push(SingleEventScreen(eventDat.eventId.toInt()))
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
                            model = eventDat.eventPicture,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(130.dp)
                                .fillMaxWidth()
                        )
                        if (currentTime in eventStartTime..eventEndTime) {
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
                        .padding(start = 7.dp, top = 4.dp, end = 7.dp, bottom = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                eventDat.eventName,
                                fontWeight = FontWeight.ExtraBold,
                                style = AppFont.InterTypography.h4,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if(eventStartTime.date != eventEndTime.date) {
                            Text(
                                "${DateTimeFormat.home_event_date_format.format(eventStartTime)} - ${DateTimeFormat.home_event_date_format.format(eventEndTime)} | ${DateTimeFormat.home_event_time_format.format(eventStartTime)} - ${DateTimeFormat.home_event_time_format.format(eventEndTime)}",
                                style = AppFont.InterTypography.body2,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "${DateTimeFormat.home_event_date_format.format(eventStartTime)} | ${DateTimeFormat.home_event_time_format.format(eventStartTime)} - ${DateTimeFormat.home_event_time_format.format(eventEndTime)}",
                                style = AppFont.InterTypography.body2,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .clickable { openMapLocationQuery(eventDat.eventLocation) },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                FontAwesomeIcons.Solid.LocationArrow, "Navigation Arrow",
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                eventDat.eventLocation,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.Blue,
                                style = AppFont.InterTypography.subtitle1
                            )
                        }
                    }
                }
            }
        }
    }
}