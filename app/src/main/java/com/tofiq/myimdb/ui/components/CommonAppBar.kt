package com.tofiq.myimdb.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonAppBar(
    title: String,
    showBackButton: Boolean = false,
    showRefreshButton: Boolean = false,
    showFilterButton: Boolean = false,
    showSearchButton: Boolean = false,
    showWishlistButton: Boolean = false,
    showGridViewButton: Boolean = false,
    isGridView: Boolean = false,
    wishlistCount: Int = 0,
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onBackClick: (() -> Unit)? = null,
    onRefreshClick: (() -> Unit)? = null,
    onFilterClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null,
    onWishlistClick: (() -> Unit)? = null,
    onGridViewToggle: (() -> Unit)? = null,
    onSearchQueryChange: ((String) -> Unit)? = null,
    onSearchActiveChange: ((Boolean) -> Unit)? = null,
    isLoading: Boolean = false
) {
    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = { onSearchQueryChange?.invoke(it) },
                    placeholder = {
                        Text("Search movies...")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
            } else {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            if (showBackButton && onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (isSearchActive) {
                // Close search button
                IconButton(
                    onClick = { onSearchActiveChange?.invoke(false) },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close search"
                    )
                }
            } else {
                // Search button
                if (showSearchButton && onSearchClick != null) {
                    IconButton(
                        onClick = onSearchClick,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
                // Filter button
                if (showFilterButton && onFilterClick != null) {
                    IconButton(
                        onClick = onFilterClick,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
                // Wishlist button
                if (showWishlistButton && onWishlistClick != null) {
                    Box {
                        IconButton(
                            onClick = onWishlistClick,
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Wishlist"
                            )
                        }
                        if (wishlistCount > 0) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-8).dp, y = 8.dp)
                            ) {
                                Text(
                                    text = if (wishlistCount > 99) "99+" else wishlistCount.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
                // Grid/List view toggle button
                if (showGridViewButton && onGridViewToggle != null) {
                    IconButton(
                        onClick = onGridViewToggle,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Switch to list view" else "Switch to grid view"
                        )
                    }
                }
                // Refresh button
                if (showRefreshButton && onRefreshClick != null) {
                    IconButton(
                        onClick = onRefreshClick,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
} 