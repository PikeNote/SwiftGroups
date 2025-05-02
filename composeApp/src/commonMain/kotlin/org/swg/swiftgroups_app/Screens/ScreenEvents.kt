package org.swg.swiftgroups_app.Screens

import EventFilterTemp
import FilterButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import compose.icons.fontawesomeicons.solid.Calendar
import compose.icons.fontawesomeicons.solid.ChevronRight
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Times
import compose.icons.fontawesomeicons.solid.Undo
import kotlinx.datetime.Instant
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
    return "${date.month.name.take(3)} ${date.dayOfMonth}, ${date.year}"
}

object ScreenEvents : Screen {
    @Composable
    override fun Content() {
        val viewModel: EventsViewModel = rememberScreenModel { EventsViewModel() }
        var searchText by rememberSaveable { mutableStateOf("") }
        var showDatePicker by remember { mutableStateOf(false) }
        var showCategoryPicker by remember { mutableStateOf(false) }
        var showClubPicker by remember { mutableStateOf(false) }
        var showTagPicker by remember { mutableStateOf(false) }

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
                textStyle = AppFont.InterTypography.h4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = { Text("Search events...", style = AppFont.InterTypography.h4) },
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
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color.Black,
                    cursorColor = Color.Black
                ),
                singleLine = true
            )

            val filters = remember(viewModel.selectedDate, viewModel.selectedCategories, viewModel.selectedClubs.isNotEmpty(),
                viewModel.selectedTags.isNotEmpty(), viewModel.showLongEvents) { listOf(
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
                    if (viewModel.selectedCategories.isNotEmpty()) "${viewModel.selectedCategories.size} Categories" else "Select Categories",
                    onClick = { showCategoryPicker = true }),
                EventFilterTemp(
                    "Select Clubs",
                    viewModel.selectedClubs.isNotEmpty(),
                    if (viewModel.selectedClubs.isNotEmpty()) "${viewModel.selectedClubs.size} Clubs" else "Select Clubs",
                    onClick =  { showClubPicker = true }),
                EventFilterTemp(
                    "Select Tags",
                    viewModel.selectedTags.isNotEmpty(),
                    if (viewModel.selectedTags.isNotEmpty()) "${viewModel.selectedTags.size} Tags" else "Select Tags",
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


            val filteredEvents by remember(viewModel.events, viewModel.showLongEvents) {
                derivedStateOf {
                    viewModel.events.filter {
                        longDate(it.start_time, it.end_time) || viewModel.showLongEvents
                    }
                }
            }

            // Events List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 70.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
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
        return Instant.parse(startTime).toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear ==
                Instant.parse(endTime).toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear
    }
}