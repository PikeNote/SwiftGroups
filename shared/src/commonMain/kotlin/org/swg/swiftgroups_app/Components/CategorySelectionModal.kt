package org.swg.swiftgroups_app.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.swg.swiftgroups_app.Fonts.AppFont
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.Times

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionModal(
    title: String,
    categories: List<String>,
    selectedCategories: Set<String>,
    onDismiss: () -> Unit,
    onCategoriesSelected: (Set<String>) -> Unit,
    onClearSelection: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = AppFont.InterTypography.headlineMedium
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        FontAwesomeIcons.Solid.Times,
                        contentDescription = "Close",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Category List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(categories, key = {it}) { category ->
                    CategoryItem(
                        category = category,
                        isSelected = category in selectedCategories,
                        onClick = {
                            val newSelection = selectedCategories.toMutableSet()
                            if (category in selectedCategories) {
                                newSelection.remove(category)
                            } else {
                                newSelection.add(category)
                            }
                            onCategoriesSelected(newSelection)
                        }
                    )
                }
            }

            // Clear Selection Button
            if (selectedCategories.isNotEmpty()) {
                OutlinedButton(
                    onClick = onClearSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color(0xFFCCCCCC))
                ) {
                    Text("Clear Selection", style = AppFont.InterTypography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            category,
            style = AppFont.InterTypography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                FontAwesomeIcons.Solid.Check,
                contentDescription = "Selected",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
} 