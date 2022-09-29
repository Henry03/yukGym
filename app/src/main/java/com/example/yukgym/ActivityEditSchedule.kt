package com.example.yukgym

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.yukgym.room.Constant
import com.example.yukgym.room.RegisterDB
import com.example.yukgym.room.Schedule
import kotlinx.android.synthetic.main.activity_edit_schedule.*


class ActivityEditSchedule : AppCompatActivity() {
    val db by lazy { RegisterDB(this) }
    private val CHANNEL_ID = "channel_notification"
    private val notificationId = 100

    private var scheduleId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule)

        setupView()
        setupListener()
    }

    fun setupView(){
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val intentType = intent.getIntExtra("intent_type", 0)
        when (intentType){
            Constant.TYPE_CREATE -> {
                btnUpdate.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                btnSave.visibility = View.GONE
                btnUpdate.visibility = View.GONE
                getNote()
            }
            Constant.TYPE_UPDATE -> {
                btnSave.visibility = View.GONE
                getNote()
            }
        }
    }
    private fun setupListener() {
        btnSave.setOnClickListener {
                db.scheduleDao().addSchedule(
                    Schedule(0,etTitle.text.toString(), etDate.text.toString(), etActivity.text.toString())
                )
                sendNotificationSave();
                finish()
        }
        btnUpdate.setOnClickListener {
                db.scheduleDao().updateSchedule(
                    Schedule(scheduleId,etTitle.text.toString(), etDate.text.toString(), etActivity.text.toString())
                )
                sendNotificationEdit();
                finish()
            }
    }

    fun getNote() {
        scheduleId = intent.getIntExtra("intent_id", 0)

            val notes = db.scheduleDao().getschedule(scheduleId)
            etTitle.setText(notes.title)
            etDate.setText(notes.date)
            etActivity.setText(notes.activity)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    
        }

    }
}
