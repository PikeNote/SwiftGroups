package org.swg.swiftgroups_app.Screens.Event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.regular.Calendar
import compose.icons.fontawesomeicons.regular.Clock
import compose.icons.fontawesomeicons.regular.Eye
import compose.icons.fontawesomeicons.regular.ShareSquare
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.Components.Event.QRCode
import org.swg.swiftgroups_app.Components.Home.Button.HorizontalLogoButton
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ArrowLeft
import org.swg.swiftgroups_app.Icons.MapPin
import org.swg.swiftgroups_app.Icons.PencilSquare
import org.swg.swiftgroups_app.Screens.BottomTabVisibilityManager
import org.swg.swiftgroups_app.Screens.Groups.GroupPage
import org.swg.swiftgroups_app.Screens.ImageScreen.ImageScreen
import org.swg.swiftgroups_app.Screens.Webview.WebviewScreen
import org.swg.swiftgroups_app.ShareManager.shareLink

class SingleEventScreen(val eventID : Long) : Screen {

    @Composable
    override fun Content() {
        val userPrefs : UserSettingsPreferences = koinInject()
        val singleEventViewModel = rememberScreenModel { SingleEventViewModel(eventID, userPrefs) }
        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(false)
        }

        val eventAPI by singleEventViewModel.eventSpecificAPI.collectAsState()

        val maxImageHeight = 210.dp
        val currentImgSize : MutableState<Dp> = remember { mutableStateOf(maxImageHeight) }

        val scrollState = rememberScrollState()
        val expansionThresholdPx = with(LocalDensity.current) { 30.dp.toPx() }

        var columnSize by remember { mutableStateOf(0) }

        val nestedScrollConnection = remember {

            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y.dp
                    val isNearTop = scrollState.value <= expansionThresholdPx

                    if(delta > 0.dp && !isNearTop) {
                        return Offset.Zero
                    }
                    val newImageSize = currentImgSize.value + delta
                    val previousImageSize = currentImgSize.value

                    currentImgSize.value = newImageSize.coerceIn(0.dp, maxImageHeight)

                    val consumedY = currentImgSize.value - previousImageSize

                    return Offset(0f, consumedY.value)
                }
            }
        }

        val imageAlpha: Float = ((currentImgSize.value) / (maxImageHeight)).coerceIn(0f, 1f)

        LaunchedEffect(Unit) {
            singleEventViewModel.updateData()
        }

        Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(bottom = 3.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(Brush.verticalGradient(colorStops = AppTheme.eventPageImage))
                    .dropShadow(
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                        shadow = Shadow(
                            radius = 0.dp,
                            color = Color.Black.copy(alpha = 0.2f),
                            offset = DpOffset(1.dp, 3.dp)
                        )
                    )
            ) {
                TextButton(
                    onClick = {
                        navigator.pop()},
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    Icon(
                        ArrowLeft, "",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                }



                Box (modifier = Modifier.padding(horizontal = 10.dp).onSizeChanged { size ->
                    columnSize = size.height
                }) {
                    Spacer(modifier = Modifier.height(currentImgSize.value + 20.dp))
                    if(eventAPI != null) {
                        AsyncImage(
                            model = "https://community.case.edu${eventAPI!!.photo_url}",
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .height(currentImgSize.value)
                                .fillMaxWidth()
                                .padding(bottom = 5.dp)
                                .padding(PaddingValues(start = 2.5.dp, end = 5.dp))
                                .clip(RoundedCornerShape(35.dp))
                                .dropShadow(
                                    shape =RoundedCornerShape(35.dp),
                                    shadow = Shadow(
                                        radius = 0.dp,
                                        color = Color.Black.copy(alpha = 0.2f),
                                        offset = DpOffset(1.dp, 3.dp)
                                    )
                                )
                                .graphicsLayer {
                                    this.alpha = imageAlpha
                                }
                                .clickable {
                                    navigator.push(ImageScreen(listOf(eventAPI!!.photo_url)))
                                }
                        )
                    } else {
                        Column (
                            modifier = Modifier
                                .height(currentImgSize.value)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(64.dp),
                                color = Color(0xFFd3d3da),
                                backgroundColor = Color(0xFF003B7F),
                            )
                        }
                    }

                    Text(
                        text = eventAPI?.event_name ?: "---",
                        style = AppFont.InterTypography.h3,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .graphicsLayer {
                                this.alpha =
                                    maxOf(0f, (0.3f - imageAlpha) / 0.3f)// Apply the calculated alpha for the text
                            }.padding(vertical = 12.dp).align(Alignment.Center),
                    )

                }
            }
            val yOffsetPx = with(LocalDensity.current) {
                val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
                kotlin.math.max(
                    (statusBarHeight + 48.dp).roundToPx() + columnSize,
                    (statusBarHeight + 42.dp).roundToPx() + columnSize)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(0, yOffsetPx)
                    }
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp).fillMaxWidth(),

                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
               Column (
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Text(
                       eventAPI?.event_type ?: "...",
                       style = AppFont.InterTypography.h6,
                       color = Color(0xFF2C58A9)
                   )
                   Text(
                       eventAPI?.event_name ?: "Loading...",
                       style = AppFont.InterTypography.h3,
                   )
                   Text(
                       eventAPI?.event_group ?: "...",
                       style = AppFont.InterTypography.h6,
                       color = Color.Gray, modifier = Modifier.clickable {
                           if(eventAPI!= null) {
                               navigator.push(GroupPage(eventAPI!!.event_group_id.toString()))
                           }
                       }
                   )
               }

                Spacer(modifier = Modifier.height(3.dp))

                if(eventAPI?.tickets != null) {
                    eventAPI!!.tickets?.forEach {
                        QRCode(
                            name = it.name,
                            ticketName = "${it.ticketName} - ${it.amount}",
                            time = it.eventDateTime,
                            code = it.paypalUid
                        ).Content()
                    }
                }

                /*

                 */

                Spacer(modifier = Modifier.height(7.dp))

                if (eventAPI != null) {
                    Row(
                        modifier = Modifier.padding(6.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {


                        HorizontalLogoButton(
                            text = "Share",
                            onClick = {
                                shareLink(eventAPI!!.share_url, eventAPI!!.event_name)
                            },
                            size = 20.dp,
                            logo = FontAwesomeIcons.Regular.ShareSquare,
                            textStyle = AppFont.InterTypography.h5,
                            backgroundColor = Color(0xFFD9D9D9),
                            textColor = Color.Black,
                            width = if(eventAPI!!.register_url=="") 340.dp else 170.dp
                        )

                        if(eventAPI!!.register_url.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(20.dp))

                            HorizontalLogoButton(
                                text = if (eventAPI!!.registered == 0) "Registration" else "Edit",
                                onClick = {
                                    val title = if (eventAPI!!.registered == 0) "Registration" else "Edit Registration"
                                    val matchUrl = if (eventAPI!!.registered == 0)
                                        "https://community.case.edu/confirmation?type=rsvp&" else "/rsvp_boot?id="

                                    navigator.push(WebviewScreen(eventAPI!!.register_url,
                                        title,
                                        matchUrl))
                                },
                                size = 20.dp,
                                logo = PencilSquare,
                                textStyle = AppFont.InterTypography.h5
                            )
                        }
                    }
                }



                Column(modifier = Modifier.padding(10.dp).fillMaxWidth()) {

                    if(!eventAPI?.registration_status.isNullOrEmpty()) {
                        LogoText(
                            logo = FontAwesomeIcons.Regular.Eye,
                            contentDesc = "Registration Status",
                            text = eventAPI?.registration_status ?: "---"
                        )
                    }

                    val text: String = if(eventAPI?.event_date != eventAPI?.event_end_date) {
                        "${eventAPI?.event_date ?: "---"} - ${eventAPI?.event_end_date  ?: "---"}"
                    } else {
                        eventAPI?.event_date ?: "---"
                    }

                    LogoText(
                        logo = FontAwesomeIcons.Regular.Calendar,
                        contentDesc = "Calendar Icon",
                        text = text
                    )

                    LogoText(
                        logo = FontAwesomeIcons.Regular.Clock,
                        contentDesc = "Clock  Icon",
                        text = "${eventAPI?.event_start_time ?: "---"} - ${eventAPI?.event_end_time  ?: "---"} ${eventAPI?.event_timezone ?: "---" }"
                    )

                    LogoText(
                        logo = MapPin,
                        contentDesc = "Map Pin Icon",
                        text = eventAPI?.location ?: "---"
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Attendees (${eventAPI?.attendees_count ?: "N/A"})",
                        style = AppFont.InterTypography.h3,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier=Modifier.height(5.dp))
                    LazyRow (
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        eventAPI?.attendees?.let { attendeeList ->
                            items(attendeeList, key = {it.user_id}) {
                                AsyncImage(
                                    model = "https://community.case.edu${it.photo_url}",
                                    contentDescription = "Person",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.height(45.dp).width(45.dp)
                                        .clip(
                                            CircleShape
                                        )
                                )
                            }
                        }

                    }
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    thickness = 2.dp,
                    color = Color.Black
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Event Details ",
                        style = AppFont.InterTypography.h3,
                        fontWeight = FontWeight.Bold
                    )

                    Text(eventAPI?.event_description ?:"")

                    if (!eventAPI?.event_tags.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Tags",
                            style = AppFont.InterTypography.h3,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(eventAPI?.event_tags ?: emptyList(), key = {it.name}) { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFEEEEEE))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        tag.name,
                                        style = AppFont.InterTypography.body2,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(230.dp))
                }

            }
        }
    }

    @Composable
    fun LogoText(logo : ImageVector, contentDesc : String, text : String) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                logo, contentDesc,
                modifier = Modifier
                    .size(30.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column (verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                Text(text)
            }
        }
    }
}