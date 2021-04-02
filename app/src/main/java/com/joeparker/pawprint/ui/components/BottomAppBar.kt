package com.joeparker.pawprint.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.joeparker.pawprint.data.constant.EntryType
import com.joeparker.pawprint.data.entity.Entry

/**
 * Bottom bar of buttons for adding each entry type. Tapping records an entry instantly, holding down opens the datetime picker
 * for recording a past or upcoming entry.
 */
@ExperimentalFoundationApi
@Composable
fun PawPrintBottomBar(addEntry: (Entry) -> Unit, selectTime: (EntryType) -> Unit) {
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