/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.joeparker.pawprint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.joeparker.pawprint.data.PawPrintDatabase
import com.joeparker.pawprint.data.constant.EntryType
import com.joeparker.pawprint.data.entity.Entry
import com.joeparker.pawprint.data.repository.EntryRepository
import com.joeparker.pawprint.ui.components.RallyTopAppBar
import com.joeparker.pawprint.ui.overview.OverviewViewModel
import com.joeparker.pawprint.ui.overview.OverviewViewModelFactory
import com.joeparker.pawprint.ui.theme.RallyTheme
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { PawPrintDatabase.getDatabase(this) }
    private val repository by lazy { EntryRepository(database.entryDAO()) }

    private val viewModel: OverviewViewModel by viewModels {
        OverviewViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allEntries.observe(this) { entries ->
            setContent {
                RallyApp(
                    entries = entries,
                    add = { viewModel.insert(Entry(UUID.randomUUID().toString(), EntryType.Sleep, if (it.isEmpty()) null else it, Date())) },
                    timeSinceLastEntry = viewModel.timeSinceLastEntry(entries.firstOrNull()),
                    refresh = { viewModel.refreshEntries() }
                )
            }
        }
    }
}

@Composable
fun RallyApp(entries: List<Entry>, add: (String) -> Unit, timeSinceLastEntry: String, refresh: () -> Unit) {
    RallyTheme {
        val allScreens = RallyScreen.values().toList()
        var currentScreen by rememberSaveable { mutableStateOf(RallyScreen.Overview) }
        Scaffold(
            topBar = {
                RallyTopAppBar(
                    allScreens = allScreens,
                    onTabSelected = { screen -> currentScreen = screen },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    RecentInfo(
                        timeSinceLastEntry = timeSinceLastEntry,
                        refresh = refresh
                    )
                    AddEntryButton(add)
                    entries.forEach {
                        EntryRow(entry = it)
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
                //currentScreen.content(onScreenChange = { screen -> currentScreen = screen })
            }
        }
    }
}

@Composable
fun RecentInfo(timeSinceLastEntry: String, refresh: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = { refresh() })
    ) {
        Text("Last entry: $timeSinceLastEntry.")
        Spacer(modifier = Modifier.size(8.dp))
        Image(
            painter = painterResource(R.drawable.ic_refresh),
            contentDescription = "Refresh"
        )
    }
}

@Composable
fun EntryRow(entry: Entry) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(entry.type.icon),
            contentDescription = entry.type.name
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(entry.notes ?: "(No Notes)")
            Text(entry.timestamp.toString())
        }
    }
}

@Composable
fun AddEntryButton(add: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Button(onClick = { add(text) }) {
        TextField(
            value = text,
            onValueChange = {
                // If user hits return, insert and reset, otherwise update the text state
                text = if (it.isNotEmpty() && it.last() == '\n') {
                    add(text)
                    ""
                } else {
                    it
                }
            },
            label = { Text("Notes") }
        )
        Text("ADD ENTRY")
    }
}
