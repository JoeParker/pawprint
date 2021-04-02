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
import androidx.compose.ui.Modifier
import com.joeparker.pawprint.data.PawPrintDatabase
import com.joeparker.pawprint.data.constant.EntryType
import com.joeparker.pawprint.data.entity.Entry
import com.joeparker.pawprint.data.repository.EntryRepository
import com.joeparker.pawprint.ui.components.PawPrintBottomBar
import com.joeparker.pawprint.ui.components.PawPrintTopBar
import com.joeparker.pawprint.ui.overview.OverviewScreen
import com.joeparker.pawprint.ui.overview.OverviewViewModel
import com.joeparker.pawprint.ui.overview.OverviewViewModelFactory
import com.joeparker.pawprint.ui.theme.AppTheme
import com.joeparker.pawprint.util.Helper

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

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allEntries.observe(this) { entries ->
            setContent {
                PawPrintApp(
                    entries = entries,
                    addEntry = { viewModel.insert(it) },
                    deleteEntry = { viewModel.delete(it) },
                    currentStatus = viewModel.currentStatus(entries),
                    timeSinceLastPee = viewModel.timeSinceEntry(entries.firstOrNull { it.type == EntryType.Pee }),
                    timeSinceLastPoop = viewModel.timeSinceEntry(entries.firstOrNull{ it.type == EntryType.Poop }),
                    timeDifference = { viewModel.timeSinceEntry(it) },
                    refresh = { viewModel.refreshEntries() },
                    selectTime = { entryType -> Helper.pickDateTime(context = this, onCompletion = { viewModel.insert(Entry(type = entryType, timestamp = it)) }) }
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun PawPrintApp(
    entries: List<Entry>,
    addEntry: (Entry) -> Unit,
    deleteEntry: (Entry) -> Unit,
    currentStatus: String,
    timeSinceLastPee: String?,
    timeSinceLastPoop: String?,
    timeDifference: (Entry) -> String?,
    refresh: () -> Unit,
    selectTime: (EntryType) -> Unit
) {
    AppTheme {
        val allScreens = PawPrintScreen.values().toList()
        var currentScreen by rememberSaveable { mutableStateOf(PawPrintScreen.Overview) }
        Scaffold(
            topBar = {
                PawPrintTopBar(
                    allScreens = allScreens,
                    onTabSelected = { screen -> currentScreen = screen },
                    currentScreen = currentScreen
                )
            },
            bottomBar = { PawPrintBottomBar(addEntry, selectTime) }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                OverviewScreen(
                    entries = entries,
                    deleteEntry = deleteEntry,
                    currentStatus = currentStatus,
                    timeSinceLastPee = timeSinceLastPee,
                    timeSinceLastPoop = timeSinceLastPoop,
                    timeDifference = timeDifference,
                    refresh = refresh
                )
                //currentScreen.content(onScreenChange = { screen -> currentScreen = screen }) // TODO: Implement with navigation/multiple screens
            }
        }
    }
}
