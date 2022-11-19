//package com.example.yukgym.volley.adapters
//
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Filter
//import android.widget.Filterable
//import android.widget.ImageButton
//import android.widget.TextView
//import androidx.cardview.widget.CardView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.yukgym.R
//import com.example.yukgym.volley.models.Profile
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import java.util.*
//import kotlin.collections.ArrayList
//
//class ProfileAdapter(private var profileList: List<Profile>, context: Context):
//    RecyclerView.Adapter<ProfileAdapter.ViewHolder>(), Filterable {
//
//    private var filteredProfileList: MutableList<Profile>
//    private val context: Context
//
//    init {
//        filteredProfileList = ArrayList(profileList)
//        this.context = context
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.item_profile, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return filteredProfileList.size
//    }
//
//    fun setProfileList(profileList: Array<Profile>){
//        this.profileList = profileList.toList()
//        filteredProfileList = profileList.toMutableList()
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val profile = filteredProfileList[position]
//        holder.tvNama.text = profile.nama
//        holder.tvNPM.text = profile.npm
//        holder.tvFakultas.text = profile.fakultas
//        holder.tvProdi.text = profile.prodi
//
//        holder.btnDelete.setOnClickListener {
//            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
//            materialAlertDialogBuilder.setTitle("Konfirmasi")
//                .setMessage("Apakah anda yakin ingin menghapus profile ini?")
//                .setNegativeButton("Batal", null)
//                .setPositiveButton("Hapus"){_,_ ->
//                    if (context is MainActivity) profile.id?.let { it1 ->
//                        context.deleteProfile(
//                            it1
//                        )
//                    }
//                }
//                .show()
//
//        }
//
//        holder.cvProfile.setOnClickListener {
//            val i = Intent(context, AddEditActivity::class.java)
//            i.putExtra("id", profile.id)
//            if(context is MainActivity)
//                context.startActivityForResult(i, MainActivity.LAUNCH_ADD_ACTIVITY)
//        }
//
//
//    }
//
//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(charSequence: CharSequence): FilterResults {
//                val charSequenceString = charSequence.toString()
//                val filtered: MutableList<Profile> = java.util.ArrayList()
//                if(charSequenceString.isEmpty()){
//                    filtered.addAll(profileList)
//                }else{
//                    for (profile in profileList){
//                        if(profile.nama.lowercase(Locale.getDefault())
//                                .contains(charSequenceString.lowercase(Locale.getDefault()))
//
//                        )filtered.add(profile)
//
//                    }
//                }
//                val filterResults = FilterResults()
//                filterResults.values = filtered
//                return filterResults
//
//            }
//
//            override fun publishResults( CharSequence: CharSequence, filterResults: FilterResults) {
//                filteredProfileList.clear()
//                filteredProfileList.addAll(filterResults.values as List<Profile>)
//                notifyDataSetChanged()
//            }
//        }
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        var tvNama: TextView
//        var tvNPM: TextView
//        var tvFakultas: TextView
//        var tvProdi: TextView
//        var btnDelete: ImageButton
//        var cvProfile: CardView
//
//        init {
//            tvNama = itemView.findViewById(R.id.tv_nama)
//            tvNPM = itemView.findViewById(R.id.tv_npm)
//            tvFakultas = itemView.findViewById(R.id.tv_fakultas)
//            tvProdi = itemView.findViewById(R.id.tv_prodi)
//            btnDelete = itemView.findViewById(R.id.btn_delete)
//            cvProfile = itemView.findViewById(R.id.cv_profile)
//        }
//
//    }
//}