package com.example.sharedexpenseapp

import androidx.annotation.WorkerThread
import com.example.sharedexpenseapp.database.Entry
import com.example.sharedexpenseapp.database.EntryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

class ApplicationRepository(private val entryDao: EntryDao, private val scope: CoroutineScope) {

    suspend fun getUsername(): String? {
        return entryDao.getUsername()
    }

    suspend fun getIsLoggedIn(): Boolean {
        return entryDao.getIsLoggedIn()
    }

    fun setUsername(name: Entry) {
        entryDao.setUsername(name)
    }

    fun setIsLoggedIn(status: Entry) {
        entryDao.setIsLoggedIn(status)
    }

}

/*
*
* TODO:
*  Will a delete all function ever be required?
*
* */