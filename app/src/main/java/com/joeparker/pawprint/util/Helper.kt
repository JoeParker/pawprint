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
                (diffInDays >= 1) -> "$diffInDays ${pluralise("day", diffInDays)} ${diffInHours - (diffInDays * 24)} ${pluralise("day", diffInHours - (diffInDays * 24))}"
                (diffInHours >= 1) -> "$diffInHours ${pluralise("hour", diffInHours)} ${diffInMin - (diffInHours * 60)} ${pluralise("minute", diffInMin - (diffInHours * 60))}"
                (diffInMin >= 1) -> "$diffInMin ${pluralise("minute", diffInMin)}"
                else -> "$diffInSec ${pluralise("second", diffInSec)}"
            }
        }

        fun pluralise(text: String, basedOnValue: Long): String {
            return if (basedOnValue.toInt() == 1) text else "${text}s"
        }
    }
}