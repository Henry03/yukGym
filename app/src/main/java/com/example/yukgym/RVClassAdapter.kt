package com.example.yukgym

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yukgym.entity.GymClass
import com.example.yukgym.room.Schedule
import kotlinx.android.synthetic.main.rv_item_class.view.*

// Attribute dan Konstruktor data kita pada adapter
class RVClassAdapter(private val data: ArrayList<GymClass>, private val listener: OnItemClickListener) : RecyclerView.Adapter<RVClassAdapter.ViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(kelas : GymClass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_item_class,parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val currentItem = data[position]
        println("-" + currentItem.category + "-")
        holder.itemView.tvCategory.text = currentItem.category
        holder.itemView.tvClassName.text = currentItem.name
        holder.itemView.tvDuration.text = currentItem.duration
        holder.itemView.imageView.setImageResource(currentItem.images)

        holder.itemView.classCard.setOnClickListener(){
            listener.onItemClick(currentItem)

        }
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(@JvmField val itemView: View) : RecyclerView.ViewHolder(itemView)

}