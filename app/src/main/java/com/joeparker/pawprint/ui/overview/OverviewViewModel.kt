package com.joeparker.pawprint.ui.overview

import androidx.lifecycle.*
import com.joeparker.pawprint.data.constant.EntryType
import com.joeparker.pawprint.data.entity.Entry
import com.joeparker.pawprint.data.repository.EntryRepository
import com.joeparker.pawprint.util.Helper
import kotlinx.coroutines.launch
import java.util.*

const val PET_NAME = "Winnie" // TODO set & store

class OverviewViewModel(private val repository: EntryRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allEntries: LiveData<List<Entry>> = repository.allEntries.asLiveData()

    /**
     * A cheap way to refresh the observation by inserting and removing an entity.
     */
    fun refreshEntries() {
        val entry = Entry(
            id = UUID.randomUUID().toString(),
            type = EntryType.Sleep,
            notes = null,
            timestamp = Date()
        )
        insert(entry)
        delete(entry)
    }

    fun isAwake(entries: List<Entry>): Boolean {
        return entries.firstOrNull { it.type == EntryType.Sleep || it.type == EntryType.Wake }?.type == EntryType.Wake
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(entry: Entry) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun delete(entry: Entry) = viewModelScope.launch {
        repository.delete(entry)
    }

    suspend fun suspendingInsert(entry: Entry) = repository.insert(entry)

    suspend fun suspendingRemove(entry: Entry) = repository.delete(entry)

    fun timeSinceEntry(entry: Entry?): String? {
        val now = Date()
        val last = entry?.timestamp ?: return null
        return Helper.timestampToReadable(now.time - last.time)
    }

    fun currentStatus(entries: List<Entry>): String {
        val awake = isAwake(entries)
        val time = timeSinceEntry(entries.firstOrNull {
            if (awake) {
                it.type == EntryType.Wake
            } else {
                it.type == EntryType.Sleep
            }
        })
        return if (time != null) {
            "$PET_NAME's been ${if (awake) "awake" else "asleep"} for $time"
        } else {
            "$PET_NAME's overview"
        }
    }
}

/**
 * Factory to take care of the lifecycle of the ViewModel
 */
class OverviewViewModelFactory(private val repository: EntryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OverviewViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}