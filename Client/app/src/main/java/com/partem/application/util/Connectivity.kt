package com.partem.application.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager

const val NOT_CONNECTED_MESSAGE = "You're not connected to the internet."

/**
 * Determines whether the device is connected to the internet via WiFi or mobile data.
 *
 * @param context The context on which to operate on.
 *
 * @return True if the device is connected to the internet, false if not.
 */
fun isConnected(context: Context): Boolean {
    val app = context as Application
    val manager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected
    val data = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected
    return wifi || data
}