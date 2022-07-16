package com.matrusri.intruderdetection

import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.longdo.mjpegviewer.MjpegView
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Firebase.messaging.subscribeToTopic("DisconnectNotification")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("subscription successful", msg)
            }
    }
    var view :MjpegView? = null
    override fun onStart(){
        super.onStart()
        val uri = URI.create("http://54.244.102.39:80/android")
        val options = IO.Options.builder()
            .setPort(80)
            .build()
        val android_socket = IO.socket(uri,options)

        FirebaseApp.initializeApp(applicationContext)

        FirebaseMessaging.getInstance().token.addOnCompleteListener{
            if(!it.isSuccessful){
                Log.d("Firebase token", "Failed")
            }
            else{
                val token = it.result
                android_socket.emit("firebase_token", token)
            }
        }

        android_socket.on(Socket.EVENT_CONNECT_ERROR) {
            val except = it[0] as Exception
            except.printStackTrace()
        }

        android_socket.connect()
        try {
            view = findViewById(R.id.footageView)
            view?.mode = MjpegView.MODE_STRETCH
            view?.setUrl("http://54.244.102.39:80/iot_footage_view")
            view?.startStream()
        }

        catch(e: Exception){
            e.printStackTrace()
        }
        Log.i("socket","connected " + android_socket.connected())
    }
}