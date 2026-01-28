package org.swg.swiftgroups_app.Screens.Groups

import FilterButton
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Times
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Screens.BottomTabVisibilityManager
import swiftgroups.shared.generated.resources.Res
import swiftgroups.shared.generated.resources.swiftgroups_title

object GroupScreen : Screen {

    private val categoryTagModifier = Modifier
        .widthIn(min = 80.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(Color(0xFFe0e7ff))
        .padding(horizontal = 1.dp)

    private val basicColor = Color(0xFF003B7F)

    private val gradientBrush = listOf(basicColor, Color(0xAA6C65F0))

    @OptIn(ExperimentalFoundationApi::class)
    val dpCacheWindow = LazyLayoutCacheWindow(ahead = 250.dp, behind = 150.dp)


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { GroupScreenViewModel() }
        val naivgator = LocalNavigator.currentOrThrow
        var searchText by rememberSaveable { mutableStateOf("") }
        val selected by viewModel._selected.collectAsState()

        val displayList by viewModel.displayList.collectAsState()

        //val state = rememberPullToRefreshState()
        //val isRefreshing by viewModel.isRefreshing.collectAsState()

        val listState = rememberLazyListState(cacheWindow = dpCacheWindow)

        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(true)
        }

        val context = LocalPlatformContext.current
        val imageLoader = remember(context) { SingletonImageLoader.get(context) }

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
                textStyle = AppFont.InterTypography.headlineMedium,
                onValueChange = {
                    searchText = it
                    viewModel.fetchGroups(searchText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xe5e5e6)),
                placeholder = { Text("Search clubs...", style=AppFont.InterTypography.headlineMedium) },
                leadingIcon = {
                    Icon(
                        FontAwesomeIcons.Solid.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(15.dp)
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchText = ""
                                viewModel.fetchGroups(searchText)
                            }
                        ) {
                            Icon(
                                FontAwesomeIcons.Solid.Times,
                                contentDescription = "Clear search",
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    focusedTextColor = Color.Black,
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

            /*
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
             */
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    userScrollEnabled = true,
                    state = listState,
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (displayList.isEmpty()) {
                        item {

                        }
                    }
                    items(displayList, key = { it.clubID }) {
                        val categories = remember(it.clubCategories) {
                            it.clubCategories.split(",").filter { cat -> cat.isNotBlank() }.take(3)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(92.dp)
                                .padding(horizontal = 8.dp)
                                .dropShadow(
                                    shape = RoundedCornerShape(20.dp),
                                    shadow = Shadow(
                                        radius = 0.dp,
                                        color = Color.Black.copy(alpha = 0.2f),
                                        offset = DpOffset(1.dp, 2.dp)
                                    )
                                )
                                .clip(RoundedCornerShape(20.dp))
                                .background(basicColor)
                                .clickable { naivgator.push(SingleGroupScreen(groupID = it.clubID)) }
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
                                imageLoader = imageLoader,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .align(Alignment.CenterStart)
                            )


                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 82.dp, end = 12.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = it.clubName,
                                    color = Color.White,
                                    style = AppFont.InterTypography.titleLarge,
                                    maxLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp) ,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(categories) { cat ->
                                        Text(
                                            text = cat,
                                            modifier = categoryTagModifier,
                                            style = AppFont.InterTypography.bodySmall,
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
            //}
        }
    }
}