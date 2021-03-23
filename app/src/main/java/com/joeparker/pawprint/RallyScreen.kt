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

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Screen state for Rally. Navigation is kept simple until a proper mechanism is available. Back
 * navigation is not supported.
 */
enum class RallyScreen(
    val icon: ImageVector,
    private val body: @Composable (() -> Unit)
) {
    Overview(
        icon = Icons.Filled.Pets,
        body = { PlaceholderScreen() }
    ),
    Accounts(
        icon = Icons.Filled.BarChart,
        body = { PlaceholderScreen() }
    ),
    Bills(
        icon = Icons.Filled.ShowChart,
        body = { PlaceholderScreen() }
    ),
    Calendar(
        icon = Icons.Filled.CalendarViewMonth,
        body = { PlaceholderScreen() }
    ),
    Settings(
        icon = Icons.Filled.Settings,
        body = { PlaceholderScreen() }
    );
}

@Composable
fun PlaceholderScreen() {
    Text("Coming Soon")
}
