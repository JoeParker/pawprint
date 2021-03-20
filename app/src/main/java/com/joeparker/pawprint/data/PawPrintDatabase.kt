package com.joeparker.pawprint.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.joeparker.pawprint.data.dao.EntryDAO
import com.joeparker.pawprint.data.entity.Entry
import kotlinx.coroutines.runBlocking
import java.util.*

@Database(
    entities = [
        Entry::class
    ],
    version = 2
)
@TypeConverters(
    com.joeparker.pawprint.util.TypeConverters::class
)
abstract class PawPrintDatabase : RoomDatabase() {

    abstract fun entryDAO(): EntryDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PawPrintDatabase? = null

        fun getDatabase(context: Context): PawPrintDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PawPrintDatabase::class.java,
                    "pawprint_database"
                )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // TODO: debug only, disable this
                .build()

                // Populate dummy data
//                DataSeeder(
//                    entryDAO = instance.entryDAO()
//                )
//                .seed()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class DataSeeder(
        private val entryDAO: EntryDAO
    ) {
         fun seed() = runBlocking {
            // Delete all content here.
            entryDAO.deleteAll()

            // Add sample entries.
            var entry = Entry(
                id = UUID.randomUUID().toString(),
                notes = "Some notes",
                timestamp = Date()
            )
            entryDAO.insert(entry)

             entry = Entry(
                 id = UUID.randomUUID().toString(),
                 notes = "Some other notes",
                 timestamp = Date()
             )
            entryDAO.insert(entry)
        }
    }
}