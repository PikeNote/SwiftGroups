package org.swg.swiftgroups_app.Screens.Event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.Divider
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
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
import compose.icons.fontawesomeicons.regular.ShareSquare
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.Components.Event.QRCode
import org.swg.swiftgroups_app.Components.Home.Button.HorizontalLogoButton
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ArrowLeft
import org.swg.swiftgroups_app.Icons.MapPin
import org.swg.swiftgroups_app.Icons.PencilSquare

class SingleEventScreen(eventID : Int) : Screen {

    private val singleEventViewModel = SingleEventViewModel(eventID);

    @Composable
    @Preview
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

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

                    // Ensure the new image size stays within the bounds
                    currentImgSize.value = newImageSize.coerceIn(0.dp, maxImageHeight)

                    val consumedY = currentImgSize.value - previousImageSize

                    return Offset(0f, consumedY.value) // Return the consumed offset
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
                    onClick = {navigator.pop()},
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        backgroundColor = Color.Transparent
                    ),
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    Icon(
                        ArrowLeft, "",
                        modifier = Modifier
                            .size(30.dp),
                        tint = Color.Black
                    )
                }

                Box (modifier = Modifier.padding(horizontal = 10.dp)) {
                    Spacer(modifier = Modifier.height(currentImgSize.value + 20.dp))
                    AsyncImage(
                        model = "https://community.case.edu${singleEventViewModel.eventSpecificAPI.photo_url}",
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
                    Text(
                        text = singleEventViewModel.eventSpecificAPI.event_name,
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
                       singleEventViewModel.eventSpecificAPI.event_type,
                       style = AppFont.InterTypography.h6,
                       color = Color(0xFF2C58A9)
                   )
                   Text(
                       singleEventViewModel.eventSpecificAPI.event_name,
                       style = AppFont.InterTypography.h3,
                   )
                   Text(
                       singleEventViewModel.eventSpecificAPI.event_group,
                       style = AppFont.InterTypography.h6,
                       color = Color.Gray
                   )
               }

                Spacer(modifier = Modifier.height(3.dp))

                if(singleEventViewModel.eventSpecificAPI.tickets != null) {
                    singleEventViewModel.eventSpecificAPI.tickets!!.forEach {
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

                    HorizontalLogoButton(
                        text = "Share",
                        onClick = {},
                        size = 20.dp,
                        logo = FontAwesomeIcons.Regular.ShareSquare,
                        textStyle = AppFont.InterTypography.h5,
                        backgroundColor = Color(0xFFD9D9D9),
                        textColor = Color.Black
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    HorizontalLogoButton(
                        text = "Registration",
                        onClick = {},
                        size = 20.dp,
                        logo = PencilSquare,
                        textStyle = AppFont.InterTypography.h5
                    )
                }

                Column(modifier = Modifier.padding(10.dp).fillMaxWidth()) {


                    logoText(
                        logo = FontAwesomeIcons.Regular.Calendar,
                        contentDesc = "Calendar Icon",
                        text = "${singleEventViewModel.eventSpecificAPI.event_date} : ${singleEventViewModel.eventSpecificAPI.event_start_time}"
                    )

                    logoText(
                        logo = FontAwesomeIcons.Regular.Clock,
                        contentDesc = "Clock  Icon",
                        text = "${singleEventViewModel.eventSpecificAPI.event_start_time} - ${singleEventViewModel.eventSpecificAPI.event_end_time} ${singleEventViewModel.eventSpecificAPI.event_timezone}"
                    )

                    logoText(
                        logo = MapPin,
                        contentDesc = "Map Pin Icon",
                        text = singleEventViewModel.eventSpecificAPI.location
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Attendees ${singleEventViewModel.eventSpecificAPI.attendees_count}",
                        style = AppFont.InterTypography.h3,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier=Modifier.height(5.dp))
                    LazyRow (
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        runBlocking {
                            singleEventViewModel.eventSpecificAPI.attendees.forEach {
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

                    Text(singleEventViewModel.eventSpecificAPI.event_description)

                    Spacer(modifier = Modifier.height(230.dp))
                }

            }
        }
    }

    @Composable
    fun logoText(logo : ImageVector, contentDesc : String, text : String) {
        Row(
            modifier = Modifier.padding(5.dp).height(30.dp),
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