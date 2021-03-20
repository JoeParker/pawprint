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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.joeparker.pawprint.data.PawPrintDatabase
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
                RallyApp(entries, add = { runBlocking { viewModel.add(Entry(UUID.randomUUID().toString(), if (it.isEmpty()) null else it, Date())) } })
            }
        }
    }
}

@Composable
fun RallyApp(entries: List<Entry>, add: (String) -> Unit) {
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
                Column {
                    AddEntryButton(add)
                    entries.forEach {
                        Text(it.notes ?: "no Notes")
                        Text(it.timestamp?.toString() ?: "no Date")
                    }
                }
                //currentScreen.content(onScreenChange = { screen -> currentScreen = screen })
            }
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
                if (it.isNotEmpty() && it.last() == '\n') {
                    add(text)
                } else {
                    text = it
                }
            },
            label = { Text("Notes") }
        )
        Text("ADD ENTRY")
    }
}
