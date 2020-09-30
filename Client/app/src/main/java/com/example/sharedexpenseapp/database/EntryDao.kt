package com.example.sharedexpenseapp.database

import androidx.room.*

private const val USERNAME_TAG = "user_name"
private const val LOGIN_TAG = "login_status"

@Dao
interface EntryDao {

    @Query("SELECT username FROM user_credentials WHERE credential_type = \"$USERNAME_TAG\"")
    fun getUsername(): String?

    @Query("SELECT is_logged_in FROM user_credentials WHERE credential_type = \"$LOGIN_TAG\"")
    fun getIsLoggedIn(): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun _setUsername(name: Entry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun _setIsLoggedIn(status: Entry)

    @Update(entity = Entry::class)
    fun setUsername(name: Entry)

    @Update(entity = Entry::class)
    fun setIsLoggedIn(status: Entry)

    //Uncomment this if there is a need to clear the credentials table
    //@Query("DELETE FROM user_credentials")
    //fun deleteAll()

}