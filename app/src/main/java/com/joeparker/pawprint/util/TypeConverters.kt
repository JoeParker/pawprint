package com.joeparker.pawprint.util

import androidx.room.TypeConverter
import java.util.*

class TypeConverters {
    /**
     * Allow us to store Date() columns in the database
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}