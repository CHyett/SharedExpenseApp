package com.partem.application

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.partem.application.util.makeNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.partem.application.mainactivity.MainActivityViewModel


class NotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        println("The token is: $token.\nPrinted from NotificationService.kt")
        MainActivityViewModel.firebaseToken = token
    }

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