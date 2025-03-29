package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Times
import org.swg.swiftgroups_app.Components.Home.EventsCard
import org.swg.swiftgroups_app.Fonts.AppFont

object ScreenEvents : Screen {
    @Composable
    override fun Content() {
        val viewModel: EventsViewModel = rememberScreenModel { EventsViewModel() }
        var searchText by remember { mutableStateOf("") }
        var selectedFilter by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "SwiftGroups",
                style = AppFont.InterTypography.h2,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Filter Buttons
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("Event Type", "Event Tags", "Past Events", "Upcoming")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    OutlinedButton(
                        onClick = { selectedFilter = if (isSelected) null else filter },
                        modifier = Modifier
                            .height(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = if (isSelected) Color(0xFFEEEEEE) else Color.Transparent,
                            contentColor = if (isSelected) Color.Black else Color(0xFF666666)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) Color.Black else Color(0xFFCCCCCC)
                        )
                    ) {
                        Text(
                            filter,
                            style = AppFont.InterTypography.body2
                        )
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.filteredEvents) { eventData ->
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
                        LaunchedEffect(Unit) {
                            viewModel.loadMoreEvents()
                        }
                    }
                }
            }
        }
    }
}