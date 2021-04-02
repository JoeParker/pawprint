package com.joeparker.pawprint.data.dao

import androidx.room.*
import com.joeparker.pawprint.data.entity.Entry
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for reading & writing Entries to the database.
 */
@Dao
interface EntryDAO {
    @Query("SELECT * FROM entry ORDER BY timestamp DESC")
    fun findAll(): Flow<List<Entry>>

    @Query("SELECT * FROM entry WHERE id IN (:entryIds)")
    fun findAllWithIds(entryIds: IntArray): Flow<List<Entry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("DELETE FROM entry")
    fun deleteAll();
}