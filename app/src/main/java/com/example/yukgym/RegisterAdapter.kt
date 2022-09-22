package com.example.yukgym

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yukgym.room.Register

class RegisterAdapter (private val notes: ArrayList<Register>, private val
listener: OnAdapterListener) :
    RecyclerView.Adapter<RegisterAdapter.NoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            NoteViewHolder {
        return NoteViewHolder(

            LayoutInflater.from(parent.context).inflate(R.layout.activity_register,parent, false)
        )
    }
    override fun getItemCount() = notes.size
    inner class NoteViewHolder( val view: View) :
        RecyclerView.ViewHolder(view)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Register>){
        notes.clear()
        notes.addAll(list)
        notifyDataSetChanged()
    }
    interface OnAdapterListener {
        fun onClick(note: Register)
        fun onUpdate(note: Register)
        fun onDelete(note: Register)
    }
}