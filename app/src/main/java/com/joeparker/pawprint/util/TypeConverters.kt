package com.joeparker.pawprint.util

import androidx.room.TypeConverter
import com.joeparker.pawprint.data.constant.EntryType
import java.util.*

class TypeConverters {
    /**
     * Allow us to store Date() columns in the database.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * Allow us to store EntryType columns in the database.
     */
    @TypeConverter
    fun toEntryType(value: String) = enumValueOf<EntryType>(value)

    @TypeConverter
    fun fromEntryType(value: EntryType) = value.name
}