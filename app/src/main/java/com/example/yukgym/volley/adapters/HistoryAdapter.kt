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

    fun setHistoryList(profileList: Array<History>){
        this.historyList = profileList.toList()
        filteredProfileList = profileList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = filteredProfileList[position]
        val idUser = context.getSharedPreferences("login", 0).getString("id", "")
        val token = context.getSharedPreferences("login", 0).getString("token", "")

        holder.etBeratBadan.text = history.berat_badan.toString()
        holder.etTanggal.text = history.tanggal
        holder.edAktivitas.text = history.aktivitas
        holder.etLamaLatihan.text = history.lama_latihan.toString()
        println(idUser)
        println(idUser.toString())
        println(token)
        println("token" + token.toString())
        holder.btnDelete.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus history ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus"){_,_ ->
                    if (context is ActivityHistory) history.id?.let { it1 ->
                        context.deleteHistory(
                            it1,
                            idUser.toString().toLong(),
                            token.toString()
                        )
                    }
                }
                .show()

        }

        holder.cvHistory.setOnClickListener {
            val i = Intent(context, ActivityAddEditHistory::class.java)
            i.putExtra("id", history.id)
            if(context is ActivityHistory)
                context.startActivityForResult(i, ActivityHistory.LAUNCH_ADD_ACTIVITY)
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
                        if(profile.berat_badan.toString().lowercase(Locale.getDefault())
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
        var etBeratBadan: TextView
        var etLamaLatihan: TextView
        var etTanggal: TextView
        var edAktivitas: TextView
        var btnDelete: ImageButton
        var cvHistory: CardView

        init {
            edAktivitas = itemView.findViewById(R.id.tv_aktivitas)
            etBeratBadan = itemView.findViewById(R.id.tv_beratBadan)
            etLamaLatihan = itemView.findViewById(R.id.tv_lamaLatihan)
            etTanggal = itemView.findViewById(R.id.tv_tanggal)
            btnDelete = itemView.findViewById(R.id.btn_delete)
            cvHistory = itemView.findViewById(R.id.cv_history)
        }

    }
}