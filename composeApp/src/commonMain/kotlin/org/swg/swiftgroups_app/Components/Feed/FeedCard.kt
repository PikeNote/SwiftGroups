package org.swg.swiftgroups_app.Components.Feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.adamglin.composeshadow.dropShadow
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Heart
import compose.icons.fontawesomeicons.regular.ShareSquare
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.Feed.Feed
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.ChatBubble
import org.swg.swiftgroups_app.Screens.ImageScreen.ImageScreen
import sh.calvin.autolinktext.rememberAutoLinkText


class FeedCard(val feed: Feed) : Screen {

    @Composable
    @Preview
    override fun Content() {
        val showComments = remember{ mutableStateOf(false) }
        val fadeOut = remember{ mutableStateOf(true) }
        val liked = remember{ mutableStateOf(feed.liked) }
        val navigator = LocalNavigator.currentOrThrow
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Column (modifier = Modifier.dropShadow(
                offsetY = 6.dp,
                blur = 3.dp,
                shape = RectangleShape,
                spread = 0.dp
            ).background(Color(0xFFFFFFFF))) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = "https://community.case.edu${feed.writerPhoto}",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .dropShadow(offsetY = 3.dp, shape = CircleShape)
                            .clip(shape = CircleShape)
                    )
                    Column(modifier = Modifier.padding(vertical = 3.dp, horizontal = 6.dp)) {
                        Text("${feed.writerFirstName} ${feed.writerLastName}", style = AppFont.InterTypography.h4)
                        Text("${feed.feedWhen} ago in ${feed.feedTypeName}")
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    AnnotatedString.rememberAutoLinkText(
                        feed.content,
                        defaultLinkStyles = TextLinkStyles(
                            SpanStyle(
                                color = Color(0xFF456CA0),
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    ),
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                if(feed.photos != null) {
                    Box {
                        val pagerState = rememberPagerState(pageCount = {
                            feed.photos.size
                        })
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth()
                                .height(210.dp)
                        ) { page ->
                            AsyncImage(
                                model = "https://community.case.edu${feed.photos[page].photo_url}",
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clickable {
                                        navigator.push(ImageScreen(feed.photos.map {it.photo_url}))
                                    }
                            )
                        }
                        if(feed.photos.size > 1) {
                            Row(
                                Modifier
                                    .height(20.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(pagerState.pageCount) { iteration ->
                                    val color =
                                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                                    Box(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly

                ) {
                    Row (modifier = Modifier.clickable{
                        CoroutineScope(Dispatchers.IO).launch{
                            CGAPI.likePost(feed.uid, liked.value == "false")
                            if(liked.value == "false") {
                                liked.value = "true"
                            } else {
                                liked.value = "false"
                            }
                        }
                    }) {
                        Icon(FontAwesomeIcons.Solid.Heart, "", modifier = Modifier.size(20.dp), tint = if (liked.value == "true") Color.Red else Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Like")
                    }
                    Row (modifier=Modifier.clickable {
                        showComments.value = true
                    }){
                        Icon(ChatBubble, "", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Comment")
                    }
                    Row {
                        Icon(
                            FontAwesomeIcons.Regular.ShareSquare,
                            "",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp).background(Color(0xFFE0E0E3)))
            if (feed.comments.isNotEmpty()) {
                Column(
                    modifier = Modifier.defaultMinSize(minHeight = 50.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                        .background(Color(0xFFE0E0E3))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = "https://community.case.edu${feed.comments[0].writerPhotoUrl}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .dropShadow(offsetY = 3.dp, shape = CircleShape)
                                .clip(shape = CircleShape)
                        )
                        Column(
                            modifier = Modifier.padding(
                                vertical = 3.dp,
                                horizontal = 6.dp
                            )
                        ) {
                            Text(
                                "${feed.comments[0].writerFirstName} ${feed.comments[0].writerLastName} • ${feed.comments[0].writeWhen}",
                                style = AppFont.InterTypography.h5
                            )
                            Text(feed.comments[0].content)
                        }
                    }
                }

            }

        }

        if(showComments.value) {
            AnimatedVisibility(
                visible = fadeOut.value,
                exit = fadeOut()
            ) {
                commentModal(onDismissRequest = {fadeOut.value=false}, feed.comments, feed.uid)

                DisposableEffect(Unit) {
                    onDispose {
                        showComments.value = false
                        fadeOut.value = true
                    }
                }
            }

        }
    }
}