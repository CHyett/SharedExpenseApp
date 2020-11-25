package com.example.sharedexpenseapp

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.example.sharedexpenseapp.util.makeNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.sharedexpenseapp.mainactivity.MainActivityViewModel


class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        println("The token is: $token")
        println("That was printed from NotificationService.kt")
        MainActivityViewModel.firebaseToken = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
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