package com.example.yukgym

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.yukgym.room.RegisterDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class editProfileFragment : Fragment() {
    val db by lazy{activity?.let { RegisterDB(it )}  }

    var sharedPreferences: SharedPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Proses menghubungkan layout fragment_mahasiswa.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = this.getActivity()?.getSharedPreferences("login", Context.MODE_PRIVATE)
        val nameTxt :TextView =  view.findViewById(R.id.tvname)
        val emailTxt :TextView =  view.findViewById(R.id.tvemail)
        val id = sharedPreferences?.getString("id", "")
        nameTxt.setText(db?.registerDao()?.getRegister(id!!.toInt())?.name)
        emailTxt.setText(db?.registerDao()?.getRegister(id!!.toInt())?.email)
    }
}