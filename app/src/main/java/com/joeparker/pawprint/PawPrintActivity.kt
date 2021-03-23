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

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
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

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun pickDateTime(onCompletion: (Date) -> Unit) {
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)
                    onCompletion(pickedDateTime.time)
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay).show()
        }

        viewModel.allEntries.observe(this) { entries ->
            setContent {
                RallyApp(
                    entries = entries,
                    addEntry = { viewModel.insert(it) },
                    deleteEntry = { viewModel.delete(it) },
                    currentStatus = viewModel.currentStatus(entries),
                    timeSinceLastPee = viewModel.timeSinceEntry(entries.firstOrNull { it.type == EntryType.Pee }),
                    timeSinceLastPoop = viewModel.timeSinceEntry(entries.firstOrNull{ it.type == EntryType.Poop }),
                    timeDifference = { viewModel.timeSinceEntry(it) },
                    refresh = { viewModel.refreshEntries() },
                    selectTime = { entryType -> pickDateTime(onCompletion = { viewModel.insert(Entry(type = entryType, timestamp = it)) }) }
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun RallyApp(
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
            bottomBar = { EntryButtons(addEntry, selectTime) }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
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
                        //AddEntryButton(addEntry)
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
                                modifier = Modifier // TODO move to CustomDivider composable
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .padding(horizontal = 64.dp)
                                    .background(color = MaterialTheme.colors.secondary)
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
                //currentScreen.content(onScreenChange = { screen -> currentScreen = screen })
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun HeldButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val contentColor by colors.contentColor(enabled)
    Surface(
        shape = shape,
        color = colors.backgroundColor(enabled).value,
        contentColor = contentColor.copy(alpha = 1f),
        border = border,
        elevation = elevation?.elevation(enabled, interactionSource)?.value ?: 0.dp,
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
            enabled = enabled,
            role = Role.Button,
            interactionSource = interactionSource,
            indication = null
        )
    ) {
        CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(
                value = MaterialTheme.typography.button
            ) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .indication(interactionSource, rememberRipple())
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun EntryButtons(addEntry: (Entry) -> Unit, selectTime: (EntryType) -> Unit) {
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
                    HeldButton(
                        onClick = {
                            addEntry(Entry(type = type))
                        },
                        onLongClick = {
                            selectTime(type)
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
    Box {
        Card(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = refresh)
                    .background(color = MaterialTheme.colors.secondary)
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Column {
                    Text(
                        currentStatus,
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Last pee: ${if (timeSinceLastPee != null) "$timeSinceLastPee ago" else "No entries found"}")
                    Text("Last poop: ${if (timeSinceLastPoop != null) "$timeSinceLastPoop ago" else "No entries found"}")
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = "Refresh",
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    }
}

@Composable
fun EntryRow(entry: Entry, deleteEntry: (Entry) -> Unit, timeDifference: (Entry) -> String?) {
    Box(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(entry.type.icon),
                contentDescription = entry.type.name
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(entry.notes ?: entry.type.name, color = MaterialTheme.colors.primary)
                Text(entry.timestamp.toString(), style = MaterialTheme.typography.subtitle2)
                timeDifference(entry)?.let {
                    Text(
                        text = if (it.firstOrNull() == '-') "Upcoming" else "$it ago",
                        style = MaterialTheme.typography.subtitle2,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .alpha(0.7f)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete",
                modifier = Modifier
                    .alpha(0.4f)
                    .clickable(onClick = { deleteEntry(entry) })
                    .padding(8.dp) // Padding to increase the 'click area' for easier use
            )
        }
    }
}

@Composable
fun AddEntryButton(addEntry: (Entry) -> Unit) {
    var text by remember { mutableStateOf("") }

    Button(
        onClick = {
            addEntry(Entry(type = EntryType.Sleep, notes = if (text.isEmpty()) null else text))
        }
    ) {
        TextField(
            value = text,
            onValueChange = {
                // If user hits return, insert and reset, otherwise update the text state
                text = if (it.isNotEmpty() && it.last() == '\n') {
                    addEntry(Entry(type = EntryType.Sleep, notes = if (text.isEmpty()) null else text))
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
