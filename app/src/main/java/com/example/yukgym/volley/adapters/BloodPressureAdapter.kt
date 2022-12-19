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
import com.example.yukgym.ActivityAddEditBloodPressure
import com.example.yukgym.ActivityBloodPressure
import com.example.yukgym.R
import com.example.yukgym.volley.models.BloodPressure
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList

class BloodPressureAdapter (private var bloodPressureList: List<BloodPressure>, context: Context):
    RecyclerView.Adapter<BloodPressureAdapter.ViewHolder>(), Filterable {

    private var filteredProfileList: MutableList<BloodPressure>
    private val context: Context

    init {
        filteredProfileList = ArrayList(bloodPressureList)
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_blood_pressure_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredProfileList.size
    }

    fun setBloodPressureList(profileList: Array<BloodPressure>){
        this.bloodPressureList = profileList.toList()
        filteredProfileList = profileList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bloodPressure = filteredProfileList[position]
        val token = context.getSharedPreferences("login", 0).getString("token", "")

        holder.etDatetime.text = bloodPressure.date_time
        holder.etBloodPressure.text = bloodPressure.systolic.toString() + "/" + bloodPressure.diastolic.toString() + " mmHg"
        holder.btnDelete.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Are you sure to delete this record?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete"){, ->
                    if (context is ActivityBloodPressure) bloodPressure.id?.let { it1 ->
                        context.deleteBloodPressure(
                            it1,
                            token.toString()
                        )
                    }
                }
                .show()

        }

        holder.btnEdit.setOnClickListener {
            val i = Intent(context, ActivityAddEditBloodPressure::class.java)
            i.putExtra("id", bloodPressure.id)
            if(context is ActivityBloodPressure)
                context.startActivityForResult(i, ActivityBloodPressure.LAUNCH_ADD_ACTIVITY)
        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<BloodPressure> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(bloodPressureList)
                }else{
                    for (profile in bloodPressureList){
                        if(profile.date_time.lowercase(Locale.getDefault())
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
                filteredProfileList.addAll(filterResults.values as List<BloodPressure>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var etBloodPressure: TextView
        var etDatetime: TextView
        var btnEdit: ImageView
        var btnDelete: ImageView

        init {
            etBloodPressure = itemView.findViewById(R.id.tvBloodPressure)
            etDatetime = itemView.findViewById(R.id.tvDateTime)
            btnEdit = itemView.findViewById(R.id.icon_edit_blood_pressure)
            btnDelete = itemView.findViewById(R.id.icon_delete_blood_pressure)
        }

    }
}
