package org.swg.swiftgroups_app.Screens.Event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.Divider
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.Components.Event.QRCode
import org.swg.swiftgroups_app.Components.Home.Button.HorizontalLogoButton
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ArrowLeft
import org.swg.swiftgroups_app.Icons.MapPin
import org.swg.swiftgroups_app.Icons.PencilSquare
import org.swg.swiftgroups_app.Screens.BottomTabVisibilityManager
import org.swg.swiftgroups_app.Screens.Groups.GroupPage
import org.swg.swiftgroups_app.Screens.Webview.WebviewScreen
import org.swg.swiftgroups_app.ShareManager.shareLink

class SingleEventScreen(eventID : Int) : Screen {

    private val singleEventViewModel = SingleEventViewModel(eventID)

    @Composable
    @Preview
    override fun Content() {
        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(false)
        }

        DisposableEffect(Unit) {
            onDispose {
                bottomTabVisibilityManager.setBottomBarVisibility(true)
            }
        }


        val eventAPI = singleEventViewModel.eventSpecificAPI.value

        val maxImageHeight = 210.dp
        val currentImgSize : MutableState<Dp> = remember { mutableStateOf(maxImageHeight) }
        val maxImageSizePx = with(LocalDensity.current) { 100.dp.toPx() }

        val scrollState = rememberScrollState()

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y.dp

                    if(delta > 0.dp && scrollState.value >=  maxImageSizePx) {
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

        Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .shadow(3.dp, shape=RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .padding(bottom = 3.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(Brush.verticalGradient(colorStops = AppTheme.eventPageImage))
            ) {
                TextButton(
                    onClick = {bottomTabVisibilityManager.setBottomBarVisibility(true)
                        navigator.pop()},
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
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

                Box (modifier = Modifier.padding(horizontal = 10.dp)) {
                    Spacer(modifier = Modifier.height(currentImgSize.value + 20.dp))
                    if(eventAPI != null) {
                        AsyncImage(
                            model = "https://community.case.edu${eventAPI.photo_url}",
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .height(currentImgSize.value)
                                .fillMaxWidth()
                                .shadow(3.dp, shape = RoundedCornerShape(35.dp))
                                .padding(bottom = 5.dp)
                                .padding(PaddingValues(start = 2.5.dp, end = 5.dp))
                                .clip(RoundedCornerShape(35.dp))
                                .graphicsLayer {
                                    this.alpha = imageAlpha
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
                            }.padding(vertical = 10.dp).align(Alignment.Center)
                    )

                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(0, kotlin.math.max(118.5.dp.roundToPx(), ((currentImgSize.value + 90.dp).roundToPx())))
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
                               navigator.push(GroupPage(eventAPI.event_group_id.toString()))
                           }
                       }
                   )
               }

                Spacer(modifier = Modifier.height(3.dp))

                if(eventAPI?.tickets != null) {
                    eventAPI.tickets.forEach {
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

                Row(
                    modifier = Modifier.padding(6.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    if (eventAPI != null) {
                        HorizontalLogoButton(
                            text = "Share",
                            onClick = {
                                shareLink(eventAPI.share_url, eventAPI.event_name)
                            },
                            size = 20.dp,
                            logo = FontAwesomeIcons.Regular.ShareSquare,
                            textStyle = AppFont.InterTypography.h5,
                            backgroundColor = Color(0xFFD9D9D9),
                            textColor = Color.Black
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        if(eventAPI.registered  == 0) {
                            HorizontalLogoButton(
                                text = "Registration",
                                onClick = {
                                    navigator.push(WebviewScreen(eventAPI.register_url,
                                        "Registration",
                                        { singleEventViewModel.updateData() },
                                        "https://community.case.edu/confirmation?type=rsvp&"))
                                },
                                size = 20.dp,
                                logo = PencilSquare,
                                textStyle = AppFont.InterTypography.h5
                            )
                        } else {
                            HorizontalLogoButton(
                                text = "Edit",
                                onClick = {
                                    navigator.push(WebviewScreen(eventAPI.register_url, "Edit Registration", { singleEventViewModel.updateData() },
                                        "/rsvp_boot?id=" ))
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
                        logoText(
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

                    logoText(
                        logo = FontAwesomeIcons.Regular.Calendar,
                        contentDesc = "Calendar Icon",
                        text = text
                    )

                    logoText(
                        logo = FontAwesomeIcons.Regular.Clock,
                        contentDesc = "Clock  Icon",
                        text = "${eventAPI?.event_start_time ?: "---"} - ${eventAPI?.event_end_time  ?: "---"} ${eventAPI?.event_timezone ?: "---" }"
                    )

                    logoText(
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
                        runBlocking {
                            eventAPI?.attendees?.forEach {
                                item {
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
                }

                Divider(
                    thickness = 2.dp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Event Details ",
                        style = AppFont.InterTypography.h3,
                        fontWeight = FontWeight.Bold
                    )

                    Text(eventAPI?.event_description ?:"")

                    Spacer(modifier = Modifier.height(230.dp))
                }

            }
        }
    }

    @Composable
    fun logoText(logo : ImageVector, contentDesc : String, text : String) {
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