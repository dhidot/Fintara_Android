package com.bcafinance.fintara.config.network

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bcafinance.fintara.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "From: ${message.from}")

        message.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${message.data}")
        }

        message.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
        // Tampilkan notifikasi
        showNotification(
            message.notification?.title,
            message.notification?.body
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
    }

    //shownotificationmessage to device
    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // HIGH = untuk heads-up
            ).apply {
                enableVibration(true)
                enableLights(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_white) // ikon notifikasi
            .setContentTitle(title ?: "Notifikasi")
            .setContentText(message ?: "")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // ini penting untuk heads-up di Android < 8
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL) // untuk bunyi & getar default
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        notificationManager.notify(0, notificationBuilder.build())
    }

}