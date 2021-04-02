package com.joeparker.pawprint.data.constant

import com.joeparker.pawprint.R

/**
 * Defines the entry types available to be recorded in the app.
 */
enum class EntryType(
    val icon: Int
) {
    Sleep(R.drawable.sleep),
    Wake(R.drawable.wake),
    Pee(R.drawable.pee),
    Poop(R.drawable.poop),
    Feed(R.drawable.feed);
}
