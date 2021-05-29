package com.partem.application

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.partem.application.util.makeNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.partem.application.mainactivity.MainActivityViewModel


class NotificationService: FirebaseMessagingService() {

    /**
     * Specifies what happens when a new Firebase token is received by the device.
     *
     * @param token The firebase token represented as a String.
     */
    override fun onNewToken(token: String) {
        println("The token is: $token.\nPrinted from NotificationService.kt")
        MainActivityViewModel.firebaseToken = token
    }

    /**
     * Specifies what happens when a notification is received from the server.
     *
     * @param message The push notification received from the server.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("onMessageReceived was called.")
        (ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager).makeNotification(message, applicationContext)
    }

}

/*
*
* TODO:
*  Check if wifi or mobile data is enabled before doing any of this
*  See if you can use RemoteMessage.notification to get notification (for efficiency) from message rather than use third party GSON
*
* */