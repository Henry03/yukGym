package com.example.yukgym

import android.app.PendingIntent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.yukgym.room.Constant
import com.example.yukgym.room.RegisterDB
import com.example.yukgym.room.Schedule
import kotlinx.android.synthetic.main.activity_edit_schedule.*
import kotlinx.android.synthetic.main.activity_schedule_adapter.*


class ActivitySelectClass : AppCompatActivity() {
    val db by lazy { RegisterDB(this) }
    private lateinit var notificationManager: NotificationManagerCompat
    private val CHANNEL_ID = "channel_notification"
    private val notificationId = 100

    private var scheduleId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_item_class)

        notificationManager = NotificationManagerCompat.from(this)
        card.setOnClickListener {
            sendNotificationSave();
            finish()
        }
    }

//    fun setupView(){
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        val intentType = intent.getIntExtra("intent_type", 0)
//        when (intentType){
//            Constant.TYPE_CREATE -> {
//                btnUpdate.visibility = View.GONE
//            }
//            Constant.TYPE_READ -> {
//                btnSave.visibility = View.GONE
//                btnUpdate.visibility = View.GONE
//                getNote()
//            }
//            Constant.TYPE_UPDATE -> {
//                btnSave.visibility = View.GONE
//                getNote()
//            }
//        }
//    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun sendNotificationSave(){
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, 0)
        val progressMax = 100
        val bigtext = etActivity.getText().toString()
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_app)
            .setContentTitle("Schedule Added")
            .setContentText(etTitle.getText().toString() + "\n" + etDate.getText().toString())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(progressMax, 0, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(-551645)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(bigtext))

        notificationManager.notify(1, builder.build())

        Thread(Runnable {
            SystemClock.sleep(2000)
            var progress = 0
            while (progress <= progressMax) {
                SystemClock.sleep(
                    1000
                )
                progress += 20
                builder.setContentText(progress.toString() + "%")
                    .setProgress(progressMax, progress, false)

            }
            builder.setContentText("Download complete")
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(1, builder.build())
        }).start()
    }
}
