package org.swg.swiftgroups_app.Screens

import EventFilterTemp
import FilterButton
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.*
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.Components.CategorySelectionModal
import org.swg.swiftgroups_app.Components.DatePickerModal
import org.swg.swiftgroups_app.Components.Home.EventsCard
import org.swg.swiftgroups_app.Fonts.AppFont
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.swiftgroups_title

private fun formatDate(date: LocalDate): String {
    return "${date.month.name.take(3)} ${date.day}, ${date.year}"
}

object ScreenEvents : Screen {
    @OptIn(ExperimentalFoundationApi::class)
    val dpCacheWindow = LazyLayoutCacheWindow(ahead = 500.dp, behind = 250.dp)

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel: EventsViewModel = rememberScreenModel { EventsViewModel() }
        var searchText by rememberSaveable { mutableStateOf("") }
        var showDatePicker by remember { mutableStateOf(false) }
        var showCategoryPicker by remember { mutableStateOf(false) }
        var showClubPicker by remember { mutableStateOf(false) }
        var showTagPicker by remember { mutableStateOf(false) }

        val isRefreshing by viewModel.isRefreshing.collectAsState()

        val state = rememberPullToRefreshState()

        val bottomTabVisibilityManager: BottomTabVisibilityManager = koinInject()
        LaunchedEffect(Unit) {
            bottomTabVisibilityManager.setBottomBarVisibility(true)
        }


        val eventListState = rememberLazyListState(cacheWindow = dpCacheWindow)

        val filteredEvents by remember(viewModel.events, viewModel.showLongEvents) {
            derivedStateOf {
                viewModel.events.filter {
                    longDate(it.start_time, it.end_time) || viewModel.showLongEvents
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.fillMaxWidth().padding(horizontal=10.dp)) {
            Image(
                painter = painterResource(Res.drawable.swiftgroups_title),
                contentDescription = "SwiftGroups Logo",
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = {
                        viewModel.setSelectedDateCal(null)
                        viewModel.updateSelectedCategories(emptySet())
                        viewModel.updateSelectedClubs(emptySet())
                        viewModel.updateSelectedTags(emptySet())
                        viewModel.filterEvents("")
                        searchText = ""
                    },
                    modifier = Modifier.align(Alignment.CenterEnd).size(30.dp)
                ) {
                    Icon(
                        FontAwesomeIcons.Solid.Undo,
                        contentDescription = "Reset Filters",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            // Search Bar
            TextField(
                value = searchText,
                onValueChange = { 
                    searchText = it
                    viewModel.filterEvents(it)
                },
                textStyle = AppFont.InterTypography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = { Text("Search events...", style = AppFont.InterTypography.headlineMedium) },
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
                                viewModel.filterEvents("")
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
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    focusedTextColor = Color.Black,
                    cursorColor = Color.Black
                ),
                singleLine = true
            )

            val filters = remember(viewModel.selectedDate, viewModel.selectedCategories, viewModel.selectedClubs,
                viewModel.selectedTags, viewModel.showLongEvents) { listOf(
                EventFilterTemp(
                    "Hide Long Events",
                    !viewModel.showLongEvents,
                    onClick = { viewModel.toggleLongEvents() }),
                EventFilterTemp(
                    "Select Date",
                    viewModel.selectedDate != null,
                    viewModel.selectedDate?.let { formatDate(it) } ?: "Select Date",
                    FontAwesomeIcons.Solid.Calendar,
                    onClick = { showDatePicker = true }) ,
                EventFilterTemp(
                    "Select Categories",
                    viewModel.selectedCategories.isNotEmpty(),
                    if (viewModel.selectedCategories.isNotEmpty()) "${viewModel.selectedCategories.size} Selected" else "Select Categories",
                    onClick = { showCategoryPicker = true }),
                EventFilterTemp(
                    "Select Clubs",
                    viewModel.selectedClubs.isNotEmpty(),
                    if (viewModel.selectedClubs.isNotEmpty()) "${viewModel.selectedClubs.size} Selected" else "Select Clubs",
                    onClick =  { showClubPicker = true }),
                EventFilterTemp(
                    "Select Tags",
                    viewModel.selectedTags.isNotEmpty(),
                    if (viewModel.selectedTags.isNotEmpty()) "${viewModel.selectedTags.size} Selected" else "Select Tags",
                    onClick =  { showTagPicker = true }),
            ) }

            // Filter Buttons
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                val listState = rememberLazyListState()
                val isScrolledToEnd by remember {
                    derivedStateOf {
                        val layoutInfo = listState.layoutInfo
                        val visibleItemsInfo = layoutInfo.visibleItemsInfo
                        if (visibleItemsInfo.isEmpty()) {
                            true
                        } else {
                            val lastVisibleItem = visibleItemsInfo.last()
                            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = listState,
                ) {
                    items(filters, key = {it.label}) { filter ->
                        FilterButton(
                            label = filter.displayLabel,
                            selected = filter.selected,
                            onClick = filter.onClick,
                            icon = filter.icon
                        )
                    }

                }

                // Only show chevron if not at the end
                if (!isScrolledToEnd) {
                    Icon(
                        FontAwesomeIcons.Solid.ChevronRight,
                        contentDescription = "Scroll for more filters",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(24.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.8f)
                                    ),
                                    startX = 0f,
                                    endX = 60f
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                }
            }

            // Events List
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel._isRefreshing.update { true }
                    viewModel.getNewEvents()
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
                    state = eventListState,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = 70.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    if (filteredEvents.isEmpty()) {
                        item {

                        }
                    }
                    items(filteredEvents, key = { it.eventId }) { eventData ->
                        EventsCard(
                            eventData,
                            cardWidth = 500.dp,
                            horizontalPadding = 16.dp
                        ).Content()
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
                    if (viewModel.hasMoreEvents && !viewModel.isLoading) {
                        item {
                            LaunchedEffect(viewModel.events.size) {
                                viewModel.loadMoreEvents()
                            }
                        }
                    }
                }
            }

        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerModal(
                onDismiss = { showDatePicker = false },
                onDateSelected = { date ->
                    viewModel.setSelectedDateCal(date)
                },
                onClearDate = {
                    viewModel.setSelectedDateCal(null)
                }
            )
        }

        // Category Selection Dialog
        if (showCategoryPicker) {
            CategorySelectionModal(
                title = "Select Categories",
                categories = viewModel.categories,
                selectedCategories = viewModel.selectedCategories,
                onDismiss = { showCategoryPicker = false },
                onCategoriesSelected = { categories ->
                    viewModel.updateSelectedCategories(categories)
                    showCategoryPicker = false
                },
                onClearSelection = {
                    viewModel.updateSelectedCategories(emptySet())
                    showCategoryPicker = false
                }
            )
        }

        // Club Selection Dialog
        if (showClubPicker) {
            CategorySelectionModal(
                title = "Select Clubs",
                categories = viewModel.clubs,
                selectedCategories = viewModel.selectedClubs,
                onDismiss = { showClubPicker = false },
                onCategoriesSelected = { clubs ->
                    viewModel.updateSelectedClubs(clubs)
                },
                onClearSelection = {
                    viewModel.updateSelectedClubs(emptySet())
                    showClubPicker = false
                }
            )
        }

        // Tag Selection Dialog
        if (showTagPicker) {
            CategorySelectionModal(
                title = "Select Tags",
                categories = viewModel.tags,
                selectedCategories = viewModel.selectedTags,
                onDismiss = { showTagPicker = false },
                onCategoriesSelected = { tags ->
                    viewModel.updateSelectedTags(tags)
                },
                onClearSelection = {
                    viewModel.updateSelectedTags(emptySet())
                    showTagPicker = false
                }
            )
        }
    }

    private fun longDate(startTime : String, endTime : String) : Boolean {
        return kotlin.time.Instant.parse(startTime).toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear ==
                kotlin.time.Instant.parse(endTime).toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear
    }
}