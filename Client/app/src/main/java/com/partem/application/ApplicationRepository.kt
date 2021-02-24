package com.partem.application

import com.partem.application.database.Entry
import com.partem.application.database.EntryDao
import kotlinx.coroutines.CoroutineScope

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