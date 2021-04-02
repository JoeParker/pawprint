package com.joeparker.pawprint.ui.overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joeparker.pawprint.data.entity.Entry


@ExperimentalFoundationApi
@Composable
fun OverviewScreen(
    entries: List<Entry>,
    deleteEntry: (Entry) -> Unit,
    currentStatus: String,
    timeSinceLastPee: String?,
    timeSinceLastPoop: String?,
    timeDifference: (Entry) -> String?,
    refresh: () -> Unit
) {
    Column {
        RecentInfo(
            currentStatus = currentStatus,
            timeSinceLastPee = timeSinceLastPee,
            timeSinceLastPoop = timeSinceLastPoop,
            refresh = refresh
        )
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            items(entries) {
                Spacer(modifier = Modifier.size(8.dp))
                EntryRow(
                    entry = it,
                    deleteEntry = deleteEntry,
                    timeDifference = timeDifference
                )
            }
            item {
                Spacer(modifier = Modifier.size(16.dp))
                Box(
                    modifier = Modifier // TODO: move to CustomDivider composable
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 64.dp)
                        .background(color = MaterialTheme.colors.secondary)
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}