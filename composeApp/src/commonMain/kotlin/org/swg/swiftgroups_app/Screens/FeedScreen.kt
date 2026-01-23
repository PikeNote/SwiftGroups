package org.swg.swiftgroups_app.Screens

import org.swg.swiftgroups_app.Components.Feed.FilterBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.Components.Feed.FeedCard
import org.swg.swiftgroups_app.Components.SpinningBar
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.swiftgroups_title

object FeedScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { FeedViewModel() }
        val state = rememberPullToRefreshState()

        val isRefreshing by viewModel.isRefreshing.collectAsState()
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

            FilterBar(buttonList, selectedIndex, onFilterSelected = {index ->
                viewModel.onFilterSelected(index)
            })

            if(feedList.isEmpty()) {
                SpinningBar(height = 100.dp, Modifier.align(Alignment.CenterHorizontally))
            } else {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        viewModel._isRefreshing.update { true }
                        viewModel.updateFeed(wipe = true)
                    },
                    state = state,
                    indicator = {
                        PullToRefreshDefaults.Indicator(
                            state = state,
                            isRefreshing = isRefreshing,
                            modifier = Modifier.align(Alignment.TopCenter),
                            containerColor = Color.White,
                            color = Color(0xFF1A73E8)
                        )
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        itemsIndexed(feedList) { index, item ->
                            key("${item.id}_${index}") {
                                FeedCard(item).Content()
                            }
                        }

                        if (!isLoading && !isRefreshing) {
                            item {
                                Spacer(modifier = Modifier.height(25.dp))
                                Column(
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF446BA0),
                                        backgroundColor = Color(0xFFCCCCCC),
                                        modifier = Modifier.height(50.dp).width(50.dp)
                                    )
                                }
                            }
                        }


                        item {
                            Spacer(modifier = Modifier.height(75.dp))
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
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

}