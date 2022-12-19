package com.example.yukgym.volley.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.yukgym.ActivityAddEditHistory
import com.example.yukgym.ActivityHistory
import com.example.yukgym.ActivityHome
import com.example.yukgym.R
import com.example.yukgym.fragment.FragmentScheduleVolley
import com.example.yukgym.volley.models.History
import com.example.yukgym.volley.models.Schedule
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.util.*
import kotlin.collections.ArrayList

class ScheduleAdapter (private var scheduleList: List<Schedule>, private val listener: OnAdapterListener):
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>(), Filterable {

    private var filteredProfileList: MutableList<Schedule>

    init {
        filteredProfileList = ArrayList(scheduleList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_schedule_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredProfileList.size
    }

    fun setScheduleList(profileList: Array<Schedule>){
        this.scheduleList = profileList.toList()
        filteredProfileList = profileList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = filteredProfileList[position]

        holder.title.text = schedule.title
        holder.activity.text = schedule.activity
        holder.date.text = schedule.date

        holder.btnDelete.setOnClickListener {
            listener.deleteSchedule(schedule)
//            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
//            materialAlertDialogBuilder.setTitle("Konfirmasi")
//                .setMessage("Apakah anda yakin ingin menghapus schedule ini?")
//                .setNegativeButton("Batal", null)
//                .setPositiveButton("Hapus"){, ->
//                    listener.deleteSchedule(
//                            idUser.toString().toLong(),
//                            token.toString()
//                        )
//
//                }
//                .show()

        }

        holder.btnEdit.setOnClickListener {
            listener.editSchedule(schedule)
        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<Schedule> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(scheduleList)
                }else{
                    for (profile in scheduleList){
                        if(profile.date.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))

                        )filtered.add(profile)

                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults

            }

            override fun publishResults( CharSequence: CharSequence, filterResults: FilterResults) {
                filteredProfileList.clear()
                filteredProfileList.addAll(filterResults.values as List<Schedule>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title: TextView
        var activity: TextView
        var date: TextView
        var btnDelete: ImageView
        var btnEdit: ImageView

        init {
            title = itemView.findViewById(R.id.tvTitle)
            activity = itemView.findViewById(R.id.tvActivity)
            date = itemView.findViewById(R.id.tvDate)
            btnDelete = itemView.findViewById(R.id.icon_delete)
            btnEdit = itemView.findViewById(R.id.icon_edit)
        }

    }

    interface OnAdapterListener{
        fun deleteSchedule(schedule: Schedule)
        fun editSchedule(schedule: Schedule)
    }
}
