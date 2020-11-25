package com.example.sharedexpenseapp.util

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.sharedexpenseapp.R
import com.google.firebase.messaging.RemoteMessage


private const val NOTIFICATION_ID = 0


/**
 * Extension function to build and deliver notifications.
 * @param remoteMessage: RemoteMessage object to be parsed through and displayed in notification.
 * @param context: activity context.
 */
fun NotificationManager.makeNotification(remoteMessage: RemoteMessage, context: Context) {

    val builder = NotificationCompat.Builder(context, remoteMessage.data["type"]!!)
        .setSmallIcon(R.drawable.partem_logo)
        .setContentTitle(remoteMessage.data["topic"])
        .setContentText(remoteMessage.data["text"])
        .setPriority(NotificationManager.IMPORTANCE_HIGH)
        .setSmallIcon(R.drawable.partem_logo)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.fuel))
    notify(NOTIFICATION_ID, builder.build())

}