package com.joeparker.pawprint.util

import java.util.concurrent.TimeUnit

class Helper {
    /**
     * Helper class for general utility functions
     */
    companion object {
        fun timestampToReadable(timestamp: Long): String {
            val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(timestamp)
            val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(timestamp)
            val diffInMin: Long = TimeUnit.MILLISECONDS.toMinutes(timestamp)
            val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(timestamp)

            return when {
                (diffInDays > 1) -> "$diffInDays days ${diffInHours - (diffInDays * 24)} hours"
                (diffInHours > 1) -> "$diffInHours hours ${diffInMin - (diffInHours * 60)} minutes"
                (diffInMin > 1) -> "$diffInMin minutes"
                else -> "$diffInSec seconds"
            }
        }
    }
}