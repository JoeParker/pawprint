package com.joeparker.pawprint.ui.overview

import androidx.lifecycle.*
import com.joeparker.pawprint.data.entity.Entry
import com.joeparker.pawprint.data.repository.EntryRepository
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class OverviewViewModel(private val repository: EntryRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allEntries: LiveData<List<Entry>> = repository.allEntries.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(entry: Entry) = viewModelScope.launch {
        repository.insert(entry)
    }

    suspend fun add(entry: Entry) = repository.insert(entry)

    fun timeSinceLastEntry(entry: Entry?): String {
        val now = Date()
        val last = entry?.timestamp ?: return "No entries found"
        val diffInMillisec: Long = now.time - last.time

        val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(diffInMillisec)
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMillisec)
        val diffInMin: Long = TimeUnit.MILLISECONDS.toMinutes(diffInMillisec)
        val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec)

        return when {
            (diffInDays > 1) -> "$diffInDays days ${diffInHours - (diffInDays * 24)} hours"
            (diffInHours > 1) -> "$diffInHours hours ${diffInMin - (diffInHours * 60)} minutes"
            (diffInMin > 1) -> "$diffInMin minutes"
            else -> "$diffInSec seconds"
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