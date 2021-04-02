package com.joeparker.pawprint.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*
import java.util.concurrent.TimeUnit

class Helper {
    /**
     * Helper class for general utility functions.
     */
    companion object {
        fun pickDateTime(context: Context, onCompletion: (Date) -> Unit) {
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            DatePickerDialog(context, { _, year, month, day ->
                TimePickerDialog(context, { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)
                    onCompletion(pickedDateTime.time)
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay).show()
        }

        fun timestampToReadable(timestamp: Long): String {
            val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(timestamp)
            val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(timestamp)
            val diffInMin: Long = TimeUnit.MILLISECONDS.toMinutes(timestamp)
            val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(timestamp)

            return when {
                (diffInDays >= 1) -> "$diffInDays ${pluralise("day", diffInDays)} ${diffInHours - (diffInDays * 24)} ${pluralise("hour", diffInHours - (diffInDays * 24))}"
                (diffInHours >= 1) -> "$diffInHours ${pluralise("hour", diffInHours)} ${diffInMin - (diffInHours * 60)} ${pluralise("minute", diffInMin - (diffInHours * 60))}"
                (diffInMin >= 1) -> "$diffInMin ${pluralise("minute", diffInMin)}"
                //(diffInSec < 0) -> "Planned for ${timestampToReadable(-timestamp)}" // TODO handle future events or disallow - this doesn't concern this function's scope
                else -> "$diffInSec ${pluralise("second", diffInSec)}"
            }
        }

        fun pluralise(text: String, basedOnValue: Long): String {
            return if (basedOnValue.toInt() == 1) text else "${text}s"
        }
    }
}