package com.partem.application.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_credentials")
class Entry(@PrimaryKey @ColumnInfo(name = "credential_type") val type: String,
                 @ColumnInfo(name = "username") val username: String?,
                 @ColumnInfo(name = "is_logged_in") val isLoggedIn: Boolean?)