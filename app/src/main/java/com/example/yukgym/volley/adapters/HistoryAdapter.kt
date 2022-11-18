package com.example.yukgym.volley.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yukgym.R
import com.example.yukgym.volley.models.History
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter (private var historyList: List<History>, context: Context):
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>(), Filterable {

    private var filteredProfileList: MutableList<History>
    private val context: Context

    init {
        filteredProfileList = ArrayList(historyList)
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredProfileList.size
    }

    fun setProfileList(profileList: Array<History>){
        this.historyList = profileList.toList()
        filteredProfileList = profileList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profile = filteredProfileList[position]
        holder.tvBeratBadan.text = history.beratBadan
        holder.tvDate.text = history.date
        holder.tvAktivitas.text = history.aktivitas
        holder.tvLamaLatihan.text = history.lamaLatihan

        holder.btnDelete.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus history ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus"){_,_ ->
                    if (context is MainActivity) profile.id?.let { it1 ->
                        context.deleteHistory(
                            it1
                        )
                    }
                }
                .show()

        }

        holder.cvProfile.setOnClickListener {
            val i = Intent(context, AddEditActivity::class.java)
            i.putExtra("id", profile.id)
            if(context is MainActivity)
                context.startActivityForResult(i, MainActivity.LAUNCH_ADD_ACTIVITY)
        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<History> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(historyList)
                }else{
                    for (profile in historyList){
                        if(profile.beratBadan.lowercase(Locale.getDefault())
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
                filteredProfileList.addAll(filterResults.values as List<History>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvBeratBadan: TextView
        var tvDate: TextView
        var tvAktivitas: TextView
        var tvLamaLatihan: TextView
        var btnDelete: ImageButton

        init {
            tvBeratBadan = itemView.findViewById(R.id.tv_beratBadan)
            tvDate = itemView.findViewById(R.id.tv_date)
            tvAktivitas = itemView.findViewById(R.id.tv_aktivitas)
            tvLamaLatihan = itemView.findViewById(R.id.tv_lamaLatihan)
            btnDelete = itemView.findViewById(R.id.btn_delete)
        }

    }
}