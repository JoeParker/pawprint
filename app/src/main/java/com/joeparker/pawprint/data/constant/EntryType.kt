package com.joeparker.pawprint.data.constant

import com.joeparker.pawprint.R

enum class EntryType(
    val icon: Int
) {
    Sleep(R.drawable.ic_sleep),
    Wake(R.drawable.ic_wake),
    Pee(1), // TODO
    Poop(2); // TODO
}
