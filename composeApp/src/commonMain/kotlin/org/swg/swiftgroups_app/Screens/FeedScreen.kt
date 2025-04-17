package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import com.adamglin.composeshadow.innerShadow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.Components.Feed.FeedCard
import org.swg.swiftgroups_app.Components.SpinningBar
import org.swg.swiftgroups_app.Fonts.AppFont
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.swiftgroups_title

object FeedScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { FeedViewModel() }
        val feedList by viewModel.feedList.collectAsState()
        val buttonList by viewModel.filterList.collectAsState()
        val selectedIndex by viewModel.selectedIndex.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(true)
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.swiftgroups_title),
                contentDescription = "SwiftGroups Logo",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row (modifier = Modifier.fillMaxWidth().padding(10.dp).height(66.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(brush = Brush.horizontalGradient(colorStops = AppTheme.profileColorStops))
                .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.padding(start = 5.dp))
                buttonList.forEachIndexed { index, item ->
                    Column (modifier = Modifier.width(80.dp).height(56.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFFF5F5F5)).then(if(index==selectedIndex) Modifier.innerShadow(
                        color = Color(0xFFc5c5c5),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                        offsetY = 5.dp,
                        blur = 5.4.dp,
                        spread = 1.dp,
                    ) else Modifier).clickable {
                        if(index != selectedIndex) {
                            viewModel._selectedIndex.update { index }
                            viewModel.updateFeed(wipe=true)
                        }
                    }, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(model = "https://community.case.edu${item.icon_url}",item.name, modifier = Modifier.size(25.dp), contentScale = ContentScale.Crop)
                        Text(item.name, style = AppFont.InterTypography.body1, maxLines=1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 3.dp))
                    }

                }
            }
            if(feedList.isEmpty()) {
                SpinningBar(height = 100.dp, Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    feedList.forEach {
                        item {
                            FeedCard(it).Content()
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(150.dp))
                    }

                    if (isLoading) {
                        item {
                            CircularProgressIndicator(
                                color = Color(0xFF446BA0),
                                backgroundColor = Color(0xFFCCCCCC),
                                modifier = Modifier.height(100.dp).fillMaxWidth()
                            )
                        }
                    }

                    if (viewModel.hasMorePosts && !isLoading) {
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.updateFeed()
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

}