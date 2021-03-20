package com.joeparker.pawprint.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "entry")
data class Entry(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "timestamp") val timestamp: Date?
)