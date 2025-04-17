package org.swg.swiftgroups_app.Components.Feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Heart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.swg.swiftgroups_app.CGAPI.CGAPI
import org.swg.swiftgroups_app.CGAPI.Feed.Comment
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.Send

@Composable
fun commentModal(onDismissRequest : () -> Unit, comments : List<Comment>, postUID : String) {

    var commentText by rememberSaveable { mutableStateOf("") }
    var commentList by remember { mutableStateOf(comments) }
    val visible = remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        visible.value = true
    }
    Scaffold (
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {

        Dialog(onDismissRequest = { visible.value = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
                    .clickable(remember { MutableInteractionSource() }, indication = null) {
                        visible.value = false
                        onDismissRequest()
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = visible.value,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight }, // start off-screen
                        animationSpec = tween(durationMillis = 500)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 500)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().height(560.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(Color.White)
                    ) {
                        LazyColumn(
                            modifier = Modifier.align(Alignment.TopCenter).offset(y = 10.dp)
                                .height(490.dp).fillMaxWidth()
                        ) {
                            if (commentList.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            "Hmm- no comments seem to be here...",
                                            style = AppFont.InterTypography.h5,
                                            color = Color.Gray,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                            commentList.forEach {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        AsyncImage(
                                            model = "https://community.case.edu${it.writerPhotoUrl}",
                                            "",
                                            modifier = Modifier.size(50.dp).clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                "${it.writerFirstName} ${it.writerLastName} • ${it.writeWhen}",
                                                style = AppFont.InterTypography.h5
                                            )
                                            Text(it.content)
                                        }
                                        Icon(
                                            FontAwesomeIcons.Solid.Heart,
                                            "",
                                            modifier = Modifier.size(25.dp)
                                                .offset(y = 10.dp).clickable {
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        CGAPI.likeComment(it.commentId, it.iLiked == 0)
                                                    }
                                                }, tint = if (it.iLiked == 0) Color.Gray else Color.Red
                                        )
                                    }
                                }
                            }

                        }

                        TextField(
                            value = commentText,
                            textStyle = AppFont.InterTypography.body1,
                            onValueChange = {
                                commentText = it
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .align(Alignment.BottomCenter),
                            placeholder = {
                                androidx.compose.material.Text(
                                    "Enter a comment...",
                                    style = AppFont.InterTypography.body1
                                )
                            },
                            enabled = enabled,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        enabled = false
                                        if (commentText.isNotEmpty()) {
                                            runBlocking {
                                                val postComment = CGAPI.postComment(
                                                    postID = postUID,
                                                    text = commentText
                                                )
                                                if (postComment) {
                                                    snackbarHostState.showSnackbar("Message sent!")
                                                    commentList =
                                                        CGAPI.getFeedComments(postUID = postUID)
                                                } else {
                                                    snackbarHostState.showSnackbar("Meassage failed to send...")
                                                }
                                                commentText = ""
                                                enabled = true
                                            }
                                        }


                                    },
                                    enabled = enabled
                                ) {
                                    androidx.compose.material.Icon(
                                        Send,
                                        contentDescription = "Send Comment",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFFF5F5F5),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                textColor = Color.Black,
                                cursorColor = Color.Black
                            ),
                            singleLine = true
                        )

                    }
                }
            }
        }

    }

}