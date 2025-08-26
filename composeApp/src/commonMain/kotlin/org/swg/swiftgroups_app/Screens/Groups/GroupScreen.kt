package org.swg.swiftgroups_app.Screens.Groups

import FilterButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Times
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Screens.BottomTabVisibilityManager
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.swiftgroups_title

object GroupScreen : Screen {

    private val categoryTagModifier = Modifier
        .widthIn(min = 80.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(Color(0xFFD9D9D9))
        .padding(horizontal = 1.dp)

    private val basicColor = Color(0xFF003B7F)

    private val gradientBrush = listOf(basicColor, Color(0xAA6C65F0))



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { GroupScreenViewModel() }
        val naivgator = LocalNavigator.currentOrThrow
        var searchText by rememberSaveable { mutableStateOf("") }
        val selected by viewModel._selected.collectAsState()

        val state = rememberPullToRefreshState()
        val isRefreshing by viewModel.isRefreshing.collectAsState()

        val listState = rememberLazyListState()

        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(true)
        }

        val displayList by viewModel.displayList.collectAsState()

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
            TextField(
                value = searchText,
                textStyle = AppFont.InterTypography.h4,
                onValueChange = {
                    searchText = it
                    viewModel.fetchGroups(searchText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = { Text("Search clubs...", style=AppFont.InterTypography.h4) },
                leadingIcon = {
                    Icon(
                        FontAwesomeIcons.Solid.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchText = ""
                            }
                        ) {
                            Icon(
                                FontAwesomeIcons.Solid.Times,
                                contentDescription = "Clear search",
                                modifier = Modifier.size(20.dp)
                            )
                        }
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

            Spacer(modifier = Modifier.height(10.dp))

            LaunchedEffect(selected, searchText) {
                listState.scrollToItem(0)
            }

            Box (modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                FilterButton(
                    label = "Show My Groups",
                    selected = selected,
                    onClick = { viewModel.toggleSelected();  searchText = ""; },
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel._isRefreshing.update { true }
                    viewModel.fetchUpdatedGroups()
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
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    userScrollEnabled = true,
                    state = listState,
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(displayList, key = { it.clubID }) {
                        val categories = remember(it.clubCategories) {
                            it.clubCategories.split(",").filter { cat -> cat.isNotBlank() }.take(3)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(92.dp)
                                .padding(horizontal = 8.dp)
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .background(basicColor)
                                .clickable { naivgator.push(GroupPage(groupID = it.clubID)) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            gradientBrush
                                        )
                                    )
                            )

                            AsyncImage(
                                model = "https://community.case.edu${it.clubLogo}",
                                contentDescription = "${it.clubName} Club Logo",
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .align(Alignment.CenterStart)
                            )


                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 82.dp, end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = it.clubName,
                                    color = Color.White,
                                    style = AppFont.InterTypography.h4,
                                    maxLines = 2,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(Modifier.width(8.dp))

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    categories.forEach { cat ->
                                        Text(
                                            text = cat,
                                            modifier = categoryTagModifier,
                                            style = AppFont.InterTypography.body1,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                    }
                    // Loading indicator
                    if (viewModel.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    // Load more when reaching the end
                    if (viewModel.hasMoreClubs && !viewModel.isLoading) {
                        item {
                            viewModel.loadMoreClubs()
                        }
                    }
                }
            }
        }
    }
}