package com.example.lookup.ui.bookmarks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.lookup.domain.bookmarks.BookmarkedLocation
import com.example.lookup.ui.components.BookmarkedLocationCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookmarkedLocationsScreen(
    bookmarkedLocations: List<BookmarkedLocation>,
    onBookmarkedLocationClick: (BookmarkedLocation) -> Unit,
    onDeleteButtonClick: (selectedItems: List<BookmarkedLocation>) -> Unit,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationIconButtonContent =
        @Composable { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isSelectedMap = remember { mutableStateMapOf<BookmarkedLocation, Boolean>() }
    val isInSelectionMode by remember(bookmarkedLocations) {
        derivedStateOf { bookmarkedLocations.any { isSelectedMap[it] == true } }
    }
    var shouldShowDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
        TopAppBar(
            title = { Text(text = "Bookmarked Locations") },
            navigationIcon = {
                IconButton(
                    onClick = onBackButtonClick,
                    content = navigationIconButtonContent
                )
            },
            actions = {
                if (isInSelectionMode) {
                    IconButton(onClick = { shouldShowDeleteDialog = true }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
        val selectedColor = MaterialTheme.colorScheme.inverseSurface

        LazyColumn {
            items(items = bookmarkedLocations, key = { it.name }) {
                val backgroundColor by remember {
                    derivedStateOf { if (isSelectedMap[it] == true) selectedColor else Color.Transparent }
                }
                val animatedBackgroundColor by animateColorAsState(
                    targetValue = backgroundColor,
                    label = "",
                    animationSpec = tween()
                )
                BookmarkedLocationCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(animatedBackgroundColor)
                        .padding(16.dp),
                    nameOfLocation = it.name,
                    imageUrlOfLocation = it.imageUrl,
                    onClick = {
                        onBookmarkedLocationClick(it)
                        if (isInSelectionMode) {
                            isSelectedMap[it] = !isSelectedMap.getOrPut(it) { false }
                        }
                    },
                    onLongClick = { isSelectedMap[it] = true }
                )
            }
            item { Spacer(modifier = Modifier.navigationBarsPadding()) }
        }
        if (shouldShowDeleteDialog) {
            val selectedItemsCount = remember(isSelectedMap) {
                isSelectedMap.values.count { it == true }
            }
            val alertDialogText = remember {
                "Are you sure you want to delete the selected " +
                        "${if (selectedItemsCount == 1) "item" else "items"}?"
            }
            val alertDialogTitle = remember {
                "Delete selected ${if (selectedItemsCount == 1) "item" else "items"}?"
            }
            val selectedItems = remember(isSelectedMap) {
                isSelectedMap
                    .filter { (_, isSelected) -> isSelected }
                    .map { (bookmarkedLocation, _) -> bookmarkedLocation }
            }
            AlertDialog(
                title = { Text(text = alertDialogTitle) },
                text = { Text(text = alertDialogText) },
                onDismissRequest = { shouldShowDeleteDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteButtonClick(selectedItems)
                            shouldShowDeleteDialog = false
                        },
                        content = { Text(text = "Okay") }
                    )
                },
                dismissButton = {
                    TextButton(
                        onClick = { shouldShowDeleteDialog = false },
                        content = { Text(text = "Cancel") }
                    )
                }
            )
        }
    }
}
