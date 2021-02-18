package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID
private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(notifTitle: String, messageBody: String, applicationContext: Context){

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)

    val detailPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id)
    )
            .setSmallIcon(R.drawable.download)
            .setContentTitle(notifTitle)
            .setContentText(messageBody)

            .setContentIntent(detailPendingIntent)
            .setAutoCancel(true)

            .addAction(
                    R.drawable.ic_assistant_black_24dp,
                    applicationContext.getString(R.string.check_status),
                    detailPendingIntent
            )

            .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())

}



