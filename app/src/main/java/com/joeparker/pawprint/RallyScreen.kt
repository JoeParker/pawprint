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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.joeparker.pawprint.data.UserData
import com.joeparker.pawprint.ui.accounts.AccountsBody
import com.joeparker.pawprint.ui.bills.BillsBody
import com.joeparker.pawprint.ui.overview.OverviewBody

/**
 * Screen state for Rally. Navigation is kept simple until a proper mechanism is available. Back
 * navigation is not supported.
 */
enum class RallyScreen(
    val icon: ImageVector,
    private val body: @Composable ((RallyScreen) -> Unit) -> Unit
) {
    Overview(
        icon = Icons.Filled.Pets,
        body = { onScreenChange -> OverviewBody(onScreenChange) }
    ),
    Accounts(
        icon = Icons.Filled.BarChart,
        body = { AccountsBody(UserData.accounts) }
    ),
    Bills(
        icon = Icons.Filled.ShowChart,
        body = { BillsBody(UserData.bills) }
    ),
    Dummy(
        icon = Icons.Filled.Settings,
        body = { BillsBody(UserData.bills) }
    );

    @Composable
    fun content(onScreenChange: (RallyScreen) -> Unit) {
        body(onScreenChange)
    }
}
