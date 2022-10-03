package com.example.yukgym

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.yukgym.databinding.FragmentClassDetailBinding
import com.example.yukgym.entity.GymClass


class FragmentClassDetail(detail: GymClass) : Fragment() {
    private lateinit var notificationManager: NotificationManagerCompat
    lateinit var rvClassAdapter: RVClassAdapter
    private val CHANNEL_ID = "channel_notification"
    private val notificationId = 100
    lateinit var _binding : FragmentClassDetailBinding
    var detailTemp : GymClass = detail;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Menghubungkan layout fragment_dosen.xml dengan fragment ini
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_class_detail, container, false)
        _binding.kelas = GymClass(
            detailTemp.id,
            detailTemp.name,
            detailTemp.category,
            detailTemp.duration,
            detailTemp.images,
            detailTemp.second
        )

        _binding.handler = FragmentClass()
//        sendNotification(kelas : GymClass)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

    }

    fun sendNotification() {
        println("saya disini")
        val channel = NotificationChannel(
            "channel1",
            "hello",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val progressMax = detailTemp.second
        val GROUP_KEY = "com.example.yukgym"
        //Creating the notification object
        val notification = NotificationCompat.Builder(requireContext(), "channel1")
            .setContentTitle(detailTemp.name + " class Running")
            .setContentText(detailTemp.duration)
            .setSmallIcon(R.drawable.logo_app)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setProgress(100, 0, true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)


        notificationManager.notify(detailTemp.id, notification.build())

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
                notificationManager.notify(detailTemp.id, notification.build())
            }
            notification.setContentText("Class Complete")
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(detailTemp.id, notification.build())
        }).start()
    }

    fun test(){
        println("test")
    }
}