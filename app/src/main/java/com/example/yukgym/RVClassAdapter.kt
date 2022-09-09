package com.example.yukgym

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yukgym.entity.Class;

// Attribute dan Konstruktor data kita pada adapter
public class RVClassAdapter (private val data: Array<Class>) : RecyclerView.Adapter<RVClassAdapter.viewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder{
        // Disini kita menghubungkan layout item recycler view kita
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_class, parent, false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int){
        // Karena kita sudah mendefinisikan dan menghubungkan view kita,
        // kita bisa memakai view tersebut dan melakukan set text pada view tersebut
        val currentItem = data[position]
        holder.Category.text = currentItem.category
        holder.ClassName.text = currentItem.name
        holder.Duration.text = currentItem.duration
        holder.Image.setImageResource(currentItem.images)
    }

    override fun getItemCount(): Int{
        // Disini kita memberitahu jumlah dari item pada recycler view kita.
        return data.size
    }

    // Kelas ini berguna untuk menghubungkan view-view yang ada pada item di recycler view kita.
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val Category : TextView = itemView.findViewById(R.id.tvCategory)
        val ClassName : TextView = itemView.findViewById(R.id.tvClassName)
        val Duration : TextView = itemView.findViewById(R.id.tvDuration)
        val Image : ImageView = itemView.findViewById(R.id.imageView)
    }
}