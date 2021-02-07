package com.example.sharedexpenseapp.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager

const val NOT_CONNECTED_MESSAGE = "You're not connected to the internet."

fun isConnected(context: Context): Boolean {
    val app = context as Application
    val manager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected
    val data = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected
    return wifi || data
}