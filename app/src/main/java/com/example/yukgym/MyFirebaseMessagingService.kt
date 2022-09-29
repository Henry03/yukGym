package com.example.yukgym

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.text.CaseMap
import android.media.RingtoneManager
import android.os.Build
import android.view.textclassifier.ConversationActions
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage : RemoteMessage){

        if(remoteMessage.notification != null){
            sendNotification(remoteMessage.notification!!.title.toString(), remoteMessage.notification!!.body.toString())
        }
    }

    private fun sendNotification(messageTitle: String, messageBody: String){
        val intent = Intent(this, MyFirebaseMessagingService::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity( this,0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "CHANNEL"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)



        val notificationBuilder = NotificationCompat.Builder( this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notification Title", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            with(NotificationManagerCompat.from(this)) {
                notify(0, notificationBuilder.build())
            }
        }

    }


}