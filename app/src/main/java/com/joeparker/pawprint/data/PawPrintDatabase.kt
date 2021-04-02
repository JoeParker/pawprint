package com.joeparker.pawprint.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.joeparker.pawprint.data.constant.EntryType
import com.joeparker.pawprint.data.dao.EntryDAO
import com.joeparker.pawprint.data.entity.Entry
import kotlinx.coroutines.runBlocking
import java.util.*

@Database(
    entities = [
        Entry::class
    ],
    version = 1
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
                .allowMainThreadQueries() // debug
                .build()

                // Populate dummy data (unused)
//                DataSeeder(
//                    entryDAO = instance.entryDAO()
//                )
//                .seed()

                // TODO: delete all entries older than x days on startup?

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
                type = EntryType.Sleep,
                notes = "Some notes",
                timestamp = Date()
            )
            entryDAO.insert(entry)

             entry = Entry(
                 id = UUID.randomUUID().toString(),
                 type = EntryType.Wake,
                 notes = "Some other notes",
                 timestamp = Date()
             )
            entryDAO.insert(entry)
        }
    }
}