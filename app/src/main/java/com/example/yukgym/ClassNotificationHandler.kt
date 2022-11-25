package com.example.yukgym

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.yukgym.entity.GymClass
import com.example.yukgym.fragment.FragmentClassDetail

class ClassNotificationHandler(private var context: FragmentClassDetail): AppCompatActivity() {
    lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Create a Notification Manager
        notificationManager = NotificationManagerCompat.from(this)

    }

    fun sendNotification(kelas : GymClass) {
        println("saya disini")
        val channel = NotificationChannel(
            "channel1",
            "hello",
            NotificationManager.IMPORTANCE_HIGH
        )
//        val manager =
//            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.createNotificationChannel(channel)

        val progressMax = kelas.second
        val GROUP_KEY = "com.example.yukgym"
        //Creating the notification object
        val notification = NotificationCompat.Builder(this, "Channel_notification")
            .setContentTitle(kelas.name + " class Running")
            .setContentText(kelas.duration)
            .setSmallIcon(R.drawable.logo_app)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setProgress(100, 0, true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)


        notificationManager.notify(kelas.id, notification.build())

        Thread(Runnable {
            SystemClock.sleep(2000)
            var progress = 0
            while(progress <= progressMax) {
                SystemClock.sleep(
                    1000
                )
                progress += 10

                println(progress.toString())
                notification.setContentText(String.format("%.1f", ((progress.toDouble()/progressMax.toDouble())*100) )+ "%")
                    .setProgress(progressMax, progress, false)
                    .setSilent(true)
                notificationManager.notify(kelas.id, notification.build())
            }
            notification.setContentText("Class Complete")
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(kelas.id, notification.build())
        }).start()
    }

    fun test(){
        println("test")
    }
}