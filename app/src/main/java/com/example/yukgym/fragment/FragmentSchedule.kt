package com.example.yukgym.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yukgym.ActivityEditSchedule
import com.example.yukgym.R
import com.example.yukgym.ScheduleAdapter
import com.example.yukgym.room.Constant
import com.example.yukgym.room.RegisterDB
import com.example.yukgym.room.Schedule
import kotlinx.android.synthetic.main.fragment_schedule.*

class FragmentSchedule : Fragment() {
    val db by lazy{activity?.let { RegisterDB(it ) }  }
    private val CHANNEL_ID = "channel_notification"
    private val notificationId = 100

    lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(arrayListOf(), object :
            ScheduleAdapter.OnAdapterListener {
            override fun onClick(schedule: Schedule) {
                intentEdit(schedule.id,Constant.TYPE_READ)
            }
            override fun onUpdate(schedule: Schedule) {
                intentEdit(schedule.id, Constant.TYPE_UPDATE)
            }
            override fun onDelete(schedule: Schedule) {
                deleteDialog(schedule)
            }
        })
//        list_schedule.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = scheduleAdapter
//        }
    }

    private fun deleteDialog(schedule : Schedule){
        val alertDialog = activity?.let { AlertDialog.Builder(it) }
        alertDialog?.apply {
            setTitle("Confirmation")
            setMessage("Are You Sure to delete this data From ${schedule.title}?")
            setNegativeButton("Cancel", DialogInterface.OnClickListener
            { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            setPositiveButton("Delete", DialogInterface.OnClickListener
            { dialogInterface, i ->
                dialogInterface.dismiss()
                db?.scheduleDao()?.deleteSchedule(schedule)
                loadData()

            })
        }
        alertDialog?.show()
    }
    override fun onStart() {
        super.onStart()
        loadData()
    }
    //untuk load data yang tersimpan pada database yang sudah create data
    fun loadData() {
        val notes = db?.scheduleDao()!!.getSchedule()
        Log.d("MainActivity","dbResponse: $notes")
        scheduleAdapter.setData( notes )
    }
    fun setupListener() {
        btnCreate.setOnClickListener{
            intentEdit(0, Constant.TYPE_CREATE)
        }
    }
    //pick data dari Id yang sebagai primary key
    fun intentEdit(noteId : Int, intentType: Int){
        startActivity(
            Intent(activity, ActivityEditSchedule::class.java)
                .putExtra("intent_id", noteId)
                .putExtra("intent_type", intentType)
        )
    }


}
