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
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
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
import java.util.*

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class PawPrintActivity : ComponentActivity() {
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
                    addEntry = { viewModel.insert(it) },
                    deleteEntry = { viewModel.delete(it) },
                    currentStatus = viewModel.currentStatus(entries),
                    timeSinceLastPee = viewModel.timeSinceEntry(entries.firstOrNull { it.type == EntryType.Pee }),
                    timeSinceLastPoop = viewModel.timeSinceEntry(entries.firstOrNull{ it.type == EntryType.Poop }),
                    refresh = { viewModel.refreshEntries() }
                )
            }
        }
    }
}

@Composable
fun RallyApp(entries: List<Entry>, addEntry: (Entry) -> Unit, deleteEntry: (Entry) -> Unit, currentStatus: String, timeSinceLastPee: String?, timeSinceLastPoop: String?, refresh: () -> Unit) {
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
            },
            bottomBar = { EntryButtons(addEntry) }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                Column {
                    RecentInfo(
                        currentStatus = currentStatus,
                        timeSinceLastPee = timeSinceLastPee,
                        timeSinceLastPoop = timeSinceLastPoop,
                        refresh = refresh
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        //AddEntryButton(addEntry)
                        entries.forEach {
                            Spacer(modifier = Modifier.size(8.dp))
                            EntryRow(entry = it, deleteEntry = deleteEntry)
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 64.dp)
                            .background(color = MaterialTheme.colors.secondary)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                }
                //currentScreen.content(onScreenChange = { screen -> currentScreen = screen })
            }
        }
    }
}

@Composable
fun EntryButtons(addEntry: (Entry) -> Unit) {
    Surface(
        modifier = Modifier
            .background(color = MaterialTheme.colors.secondary)
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.secondary)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EntryType.values().forEach { type ->
                Box {
                    Button(
                        onClick = {
                            addEntry(Entry(UUID.randomUUID().toString(), type, null, Date()))
                        }
                    ) {
                        Image(
                            painter = painterResource(type.icon),
                            contentDescription = type.name,
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier
                                .height(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentInfo(currentStatus: String, timeSinceLastPee: String?, timeSinceLastPoop: String?, refresh: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = refresh)
            .background(color = MaterialTheme.colors.secondary)
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column {
            Text(currentStatus, style = MaterialTheme.typography.h5, color = MaterialTheme.colors.primary)
            Text("Last pee: ${if (timeSinceLastPee != null) "$timeSinceLastPee ago" else "No entries found"}")
            Text("Last poop: ${if (timeSinceLastPoop != null) "$timeSinceLastPoop ago" else "No entries found"}")
        }
        Box {
            Column {
                Spacer(modifier = Modifier.size(44.dp))
                Image(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = "Refresh"
                )
            }
        }
    }
}

@Composable
fun EntryRow(entry: Entry, deleteEntry: (Entry) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(entry.type.icon),
            contentDescription = entry.type.name
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(entry.notes ?: "(${entry.type.name})")
            Text(entry.timestamp.toString(), style = MaterialTheme.typography.subtitle2)
        }
        Spacer(modifier = Modifier.size(72.dp)) // TODO how to right-align image?
        Image(
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = "Delete",
            modifier = Modifier
                .alpha(0.4f)
                .clickable(onClick = { deleteEntry(entry) })
        )
    }
}

@Composable
fun AddEntryButton(addEntry: (Entry) -> Unit) {
    var text by remember { mutableStateOf("") }

    Button(
        onClick = {
            addEntry(Entry(UUID.randomUUID().toString(), EntryType.Sleep, if (text.isEmpty()) null else text, Date()))
        }
    ) {
        TextField(
            value = text,
            onValueChange = {
                // If user hits return, insert and reset, otherwise update the text state
                text = if (it.isNotEmpty() && it.last() == '\n') {
                    addEntry(Entry(UUID.randomUUID().toString(), EntryType.Sleep, if (text.isEmpty()) null else text, Date()))
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
