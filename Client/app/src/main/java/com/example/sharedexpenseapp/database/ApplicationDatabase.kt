package com.example.sharedexpenseapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Entry::class], version = 1, exportSchema = false)
abstract class ApplicationDatabase: RoomDatabase() {

    abstract fun entryDao(): EntryDao

    private class ApplicationDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                scope.launch(Dispatchers.IO) { populateDatabase(it.entryDao()) }
            }
        }

        fun populateDatabase(dao: EntryDao) {
            val initialUsername = Entry("user_name", "", null)
            val initialLoginStatus = Entry("login_status", null, false)
            scope.launch(Dispatchers.IO) {
                dao._setUsername(initialUsername)
                dao._setIsLoggedIn(initialLoginStatus)
            }
        }

    }

    companion object {

        @Volatile
        private var INSTANCE: ApplicationDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ApplicationDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApplicationDatabase::class.java,
                    "application_database")
                    .addCallback(ApplicationDatabaseCallback(scope))
                    .fallbackToDestructiveMigration().
                    build()
                INSTANCE = instance
                return instance
            }
        }

    }

}

/*
*
* TODO:
*  Google suggests setting the exportSchema to true for better version control. (Find out why here -> https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#7)
*
* */