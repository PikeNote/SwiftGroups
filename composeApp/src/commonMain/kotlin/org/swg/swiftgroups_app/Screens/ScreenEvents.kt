package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.TextButton
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
import compose.icons.fontawesomeicons.solid.Calendar
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Times
import org.swg.swiftgroups_app.Components.Home.EventsCard
import org.swg.swiftgroups_app.Fonts.AppFont
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import kotlinx.datetime.Instant
import androidx.compose.material3.DatePickerDialog

private fun formatDate(date: LocalDate): String {
    return "${date.month.name.take(3)} ${date.dayOfMonth}, ${date.year}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onClearDate: () -> Unit,
    selectedDate: LocalDate? = null
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.let {
            val now = Clock.System.now()
            val todayStart = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val daysDiff = todayStart.until(selectedDate, DateTimeUnit.DAY)
            val startOfToday = now.toEpochMilliseconds() / (24 * 60 * 60 * 1000L) * (24 * 60 * 60 * 1000L)
            startOfToday + (daysDiff - 1) * 24 * 60 * 60 * 1000L
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = Instant.fromEpochMilliseconds(it + 24 * 60 * 60 * 1000L)
                        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        onDateSelected(localDateTime.date)
                    }
                    onDismissRequest()
                }
            ) {
                Text(
                    "OK",
                    color = Color(0xFF1A73E8),
                    style = AppFont.InterTypography.button
                )
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis = null
                        onClearDate()
                        onDismissRequest()
                    }
                ) {
                    Text(
                        "Clear",
                        color = Color(0xFF1A73E8),
                        style = AppFont.InterTypography.button
                    )
                }
                TextButton(onClick = onDismissRequest) {
                    Text(
                        "Cancel",
                        color = Color(0xFF1A73E8),
                        style = AppFont.InterTypography.button
                    )
                }
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        )
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                selectedDayContainerColor = Color(0xFF1A73E8),
                todayDateBorderColor = Color(0xFF1A73E8),
                todayContentColor = Color(0xFF1A73E8),
                selectedDayContentColor = Color.White,
                currentYearContentColor = Color(0xFF1A73E8),
                selectedYearContainerColor = Color(0xFF1A73E8).copy(alpha = 0.12f),
                weekdayContentColor = Color(0xFF666666)
            )
        )
    }
}

object ScreenEvents : Screen {
    @Composable
    override fun Content() {
        val viewModel: EventsViewModel = rememberScreenModel { EventsViewModel() }
        var searchText by remember { mutableStateOf("") }
        var showDatePicker by remember { mutableStateOf(false) }

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
                val filters = listOf("Hide Long Events", "Select Date")
                items(filters) { filter ->
                    val isSelected = when (filter) {
                        "Hide Long Events" -> !viewModel.showLongEvents
                        "Select Date" -> viewModel.selectedDate != null
                        else -> false
                    }
                    OutlinedButton(
                        onClick = {
                            when (filter) {
                                "Hide Long Events" -> viewModel.toggleLongEvents()
                                "Select Date" -> showDatePicker = true
                            }
                        },
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
                        if (filter == "Select Date") {
                            Icon(
                                FontAwesomeIcons.Solid.Calendar,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            if (filter == "Select Date" && viewModel.selectedDate != null) {
                                formatDate(viewModel.selectedDate!!)
                            } else {
                                filter
                            },
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

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                onDateSelected = { date ->
                    viewModel.setSelectedDate(date)
                },
                onClearDate = {
                    viewModel.setSelectedDate(null)
                },
                selectedDate = viewModel.selectedDate
            )
        }
    }
}