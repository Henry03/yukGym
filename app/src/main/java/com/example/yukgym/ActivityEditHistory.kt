package com.example.yukgym

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import com.example.yukgym.room.Constant
import com.example.yukgym.room.RegisterDB
import com.example.yukgym.volley.models.History
import kotlinx.android.synthetic.main.activity_edit_history.*
import kotlinx.android.synthetic.main.fragment_history.*

class ActivityEditHistory : AppCompatActivity() {
    val db by lazy { RegisterDB(this) }
    private val CHANNEL_ID = "channel_notification"
    private val notificationId = 100

    private var list_history: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_history)

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
            db.historyDao().addHistory(
                History(0,etBeratBadan.text.toString(), etDate.text.toString(), etAktivitas.text.toString())
            )
            sendNotificationSave();
            finish()
        }
        btnUpdate.setOnClickListener {
            db.historyDao().updateHistory(
                History(list_history,etAktivitas.text.toString(), etDate.text.toString(), etAktivitas.text.toString())
            )
            sendNotificationEdit();
            finish()
        }
    }

    fun getNote() {
        list_history = intent.getIntExtra("intent_id", 0)

        val notes = db.historyDao().gethistory(list_history)
        etBeratBadan.setText(notes.title)
        etDate.setText(notes.date)
        etAktivitas.setText(notes.activity)
        etLamaLatihan..setText(notes.activity)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun sendNotificationSave(){
        val bigtext = etAktivitas.getText().toString()
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_app)
            .setContentTitle("Progress Added")
            .setContentText(etBeratBadan.getText().toString() + "\n" + etDate.getText().toString())
            .setColor(-551645)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(bigtext))
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }

    }

    private fun sendNotificationEdit(){
        val bigtext = etAktivitas.getText().toString() + "\n" + etDate.getText().toString()
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_app)
            .setContentTitle("Progress Updated")
            .setContentText(etBeratBadan.getText().toString())
            .setColor(-551645)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(bigtext))
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())

        }

    }
}
