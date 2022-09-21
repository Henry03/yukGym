package com.example.yukgym

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.yukgym.room.Register

class RegisterAdapter (private val listener: ActivityRegister.OnAdapterListener) : AppCompatActivity() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisterViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_note,parent, false)
        )
    }

    override fun onBindViewHolder() {
        val note = notes[position]
        holder.view.text_title.text = note.title
        holder.view.text_title.setOnClickListener{
            listener.onClick(note)
        }
        holder.view.icon_edit.setOnClickListener {
            listener.onUpdate(note)
        }
        holder.view.icon_delete.setOnClickListener {
            listener.onDelete(note)
        }
    }

    override fun getItemCount() = register.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<register>){
        register.clear()
        register.addAll(list)
        notifyDataSetChanged()
    }
    interface OnAdapterListener {
        fun onClick(register: Register)
        fun onUpdate(register: Register)
        fun onDelete(register: Register)
    }
}