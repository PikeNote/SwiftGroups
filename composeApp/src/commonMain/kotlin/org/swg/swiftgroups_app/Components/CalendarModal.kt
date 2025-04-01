package org.swg.swiftgroups_app.Components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.swg.swiftgroups_app.Fonts.AppFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    onClearDate: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = Instant.fromEpochMilliseconds(it + 24 * 60 * 60 * 1000L)
                        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        onDateSelected(localDateTime.date)
                    }
                    onDismiss()
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
                        onDismiss()
                    }
                ) {
                    Text(
                        "Clear",
                        color = Color(0xFF1A73E8),
                        style = AppFont.InterTypography.button
                    )
                }
                TextButton(onClick = onDismiss) {
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
            state = datePickerState
        )
    }
}