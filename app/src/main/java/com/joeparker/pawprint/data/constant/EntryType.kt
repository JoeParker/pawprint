package com.joeparker.pawprint.data.constant

import com.joeparker.pawprint.R

enum class EntryType(
    val icon: Int
) {
    Sleep(R.drawable.sleep),
    Wake(R.drawable.wake),
    Pee(R.drawable.pee),
    Poop(R.drawable.poop),
    Feed(R.drawable.feed);
}
