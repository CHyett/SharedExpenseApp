package com.example.sharedexpenseapp.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.example.sharedexpenseapp.ApplicationRepository
import com.example.sharedexpenseapp.NotificationReceiver
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.database.ApplicationDatabase
import com.example.sharedexpenseapp.mainactivity.MainActivityViewModel
import com.google.firebase.messaging.RemoteMessage


private const val NOTIFICATION_ID = 0


/**
 * Extension function to build and deliver notifications.
 * @param remoteMessage: RemoteMessage object to be parsed through and displayed in notification.
 * @param context: activity context.
 */
fun NotificationManager.makeNotification(remoteMessage: RemoteMessage, context: Context) {
    val type = remoteMessage.data["type"]!!
    if(type == "0") {
        val acceptBroadcastIntent = Intent(context, NotificationReceiver::class.java)
        acceptBroadcastIntent.putExtra("userFrom", remoteMessage.data["userFrom"])
        acceptBroadcastIntent.putExtra("userTo", MainActivityViewModel.user.value)
        acceptBroadcastIntent.putExtra("reply", true)
        acceptBroadcastIntent.putExtra("group", remoteMessage.data["group"])
        val acceptActionIntent = PendingIntent.getBroadcast(context, 0, acceptBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val declineBroadcastIntent = Intent(context, NotificationReceiver::class.java)
        declineBroadcastIntent.putExtra("userFrom", remoteMessage.data["userFrom"])
        declineBroadcastIntent.putExtra("userTo", MainActivityViewModel.user.value)
        declineBroadcastIntent.putExtra("reply", false)
        declineBroadcastIntent.putExtra("group", remoteMessage.data["group"])
        val declineActionIntent = PendingIntent.getBroadcast(context, 0, declineBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, remoteMessage.data["type"]!!)
            .setSmallIcon(R.drawable.partem_logo)
            .setContentTitle(remoteMessage.data["topic"])
            .setContentText(remoteMessage.data["text"])
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setSmallIcon(R.mipmap.app_icon_round)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.fuel))
            .addAction(R.mipmap.ic_launcher, "Accept", acceptActionIntent)
            .addAction(R.mipmap.app_icon, "Decline", declineActionIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setColor(0x01021E)
        notify(NOTIFICATION_ID, builder.build())
    } else {

        val builder = NotificationCompat.Builder(context, remoteMessage.data["type"]!!)
            .setSmallIcon(R.drawable.partem_logo)
            .setContentTitle(remoteMessage.data["topic"])
            .setContentText(remoteMessage.data["text"])
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setSmallIcon(R.drawable.partem_logo)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.fuel))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setColor(0x01021E)
        notify(NOTIFICATION_ID, builder.build())

    }

}

/*
*
* Notification type table
*
*
* */