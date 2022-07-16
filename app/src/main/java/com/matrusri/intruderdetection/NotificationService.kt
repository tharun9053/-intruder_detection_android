package com.matrusri.intruderdetection

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.socket.client.IO
import java.net.URI

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val uri = URI.create("http://54.244.102.39:80/android")
        val options = IO.Options.builder()
            .setPort(80)
            .build()
        val android_socket = IO.socket(uri,options)
        android_socket.connect()
        android_socket.on("connect") {
            android_socket.emit("firebase_token", p0)
        }
    }
    fun showNotification(title: String?, message: String?) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("sairam",
                "device_notifications",
                NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, "sairam")
            .setSmallIcon(R.mipmap.ic_launcher) // notification icon
            .setContentTitle(title) // title for notification
            .setContentText(message)// message for notification
            .setAutoCancel(true) // clear notification after click
        mNotificationManager.notify(0, mBuilder.build())
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        showNotification(p0.notification?.title, p0.notification?.body)
    }
}