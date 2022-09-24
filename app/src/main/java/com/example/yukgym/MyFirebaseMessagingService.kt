package com.example.yukgym

import android.app.PendingIntent
import android.content.Intent
import android.icu.text.CaseMap
import android.view.textclassifier.ConversationActions
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

class MyFirebaseMessagingService : MyFirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage : RemoteMessage){

        if(remoteMessage.notification != null){
            sendNotification(remoteMessage.notification!!.title.toString(), remoteMessage.notification!!.body.toString())
        }
    }

    private fun sendNotification(messageTitle: String, messageBody: String){
        val intent = Intent(this, MyFirebaseMessagingService::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(this, 0. intent, PendingIntent.FLAG_MUTABLE)


        val defa

    }

}