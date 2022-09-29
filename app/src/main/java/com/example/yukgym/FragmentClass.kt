package com.example.yukgym

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yukgym.entity.GymClass
import kotlinx.android.synthetic.main.activity_edit_schedule.*
import kotlinx.android.synthetic.main.fragment_class.*


class FragmentClass : Fragment() {
    private lateinit var notificationManager: NotificationManagerCompat
    lateinit var rvClassAdapter: RVClassAdapter
    private val CHANNEL_ID = "channel_notification"
    private val notificationId = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Menghubungkan layout fragment_dosen.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_class, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        rvClassAdapter = RVClassAdapter(GymClass.listOfClass, object:
            RVClassAdapter.OnItemClickListener{
            override fun onItemClick(kelas : com.example.yukgym.entity.GymClass) {
                notificationManager = NotificationManagerCompat.from(requireContext())
                sendNotification(kelas)
            }
        })

        rv_class.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvClassAdapter
        }
    }

    fun sendNotification(kelas : GymClass) {
        val channel = NotificationChannel(
            "channel1",
            "hello",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager =
            requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val progressMax = kelas.second
        //Creating the notification object
        val notification = NotificationCompat.Builder(requireContext(), "channel1")
            .setContentTitle(kelas.name + " class Running")
            .setContentText(kelas.duration)
            .setSmallIcon(R.drawable.logo_app)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setProgress(100, 0, true)
            .setAutoCancel(true)


        notificationManager.notify(1, notification.build())

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
                notificationManager.notify(1, notification.build())
            }
            notification.setContentText("Class Complete")
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(1, notification.build())
        }).start()
    }
}