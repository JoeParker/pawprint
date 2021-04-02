package com.joeparker.pawprint.ui.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.joeparker.pawprint.R
import com.joeparker.pawprint.data.constant.EntryType
import com.joeparker.pawprint.data.entity.Entry

/**
 * The recent info component with relevant information
 */
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

/**
 * A row for a single entry record, with contextual information and delete option.
 */
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

/**
 * Button for adding a custom Entry (unused).
 */
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