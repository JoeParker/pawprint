package com.joeparker.pawprint.data.repository

import com.joeparker.pawprint.data.dao.EntryDAO
import com.joeparker.pawprint.data.entity.Entry
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class EntryRepository(private val entryDAO: EntryDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allEntries: Flow<List<Entry>> = entryDAO.findAll()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    suspend fun insert(entry: Entry) {
        entryDAO.insert(entry)
    }

    suspend fun delete(entry: Entry) {
        entryDAO.delete(entry)
    }
}