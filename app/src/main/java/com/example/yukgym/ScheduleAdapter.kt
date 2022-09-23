package com.example.yukgym

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yukgym.room.Schedule
import kotlinx.android.synthetic.main.activity_schedule_adapter.view.*

class ScheduleAdapter (private val schedule: ArrayList<Schedule>, private val listener: OnAdapterListener) :
    RecyclerView.Adapter<ScheduleAdapter.NoteViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_schedule_adapter,parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = schedule[position]
        holder.view.tvTitle.text = note.title
        holder.view.tvDate.text = note.date
        holder.view.tvActivity.text = note.activity

        holder.view.card.setOnClickListener{
            listener.onClick(note)
        }
        holder.view.icon_edit.setOnClickListener {
            listener.onUpdate(note)
        }
        holder.view.icon_delete.setOnClickListener {
            listener.onDelete(note)
        }
    }

    override fun getItemCount() = schedule.size
    inner class NoteViewHolder( val view: View) : RecyclerView.ViewHolder(view)
    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Schedule>){
        schedule.clear()
        schedule.addAll(list)
        notifyDataSetChanged()
    }
    interface OnAdapterListener {
        fun onClick(schedule : Schedule)
        fun onUpdate(schedule : Schedule)
        fun onDelete(schedule : Schedule)
    }
}