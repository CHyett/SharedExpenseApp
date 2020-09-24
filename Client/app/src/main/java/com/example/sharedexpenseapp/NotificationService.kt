package com.example.sharedexpenseapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class NotificationService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        println("The token is: $token")
        println("That was printed from NotificationService.kt")
        MainActivityViewModel.firebaseToken = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("Message was: ${message.data}")
    }
}

/*
*
* TODO:
*  Check if wifi or mobile data is enabled before doing any of this
*
* */