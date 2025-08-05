package com.tofiq.myimdb.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
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
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onBackClick: (() -> Unit)? = null,
    onRefreshClick: (() -> Unit)? = null,
    onFilterClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null,
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