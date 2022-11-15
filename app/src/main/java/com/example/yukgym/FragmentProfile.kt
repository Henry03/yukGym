package com.example.yukgym

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.yukgym.room.RegisterDB

class FragmentProfile : Fragment() {
    val db by lazy{activity?.let { RegisterDB(it )}  }

    var sharedPreferences: SharedPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Proses menghubungkan layout fragment_profile.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = this.getActivity()?.getSharedPreferences("login", Context.MODE_PRIVATE)
        val nameTxt :TextView =  view.findViewById(R.id.tvname)
        val emailTxt :TextView =  view.findViewById(R.id.tvemail)
        val btnEdit : Button = view.findViewById(R.id.btnEdit)
        val id = sharedPreferences?.getString("id", "")
        nameTxt.setText(db?.registerDao()?.getRegister(id!!.toInt())?.name)
        emailTxt.setText(db?.registerDao()?.getRegister(id!!.toInt())?.email)

        btnEdit.setOnClickListener(){
            (activity as ActivityHome).setActivity(ActivityEditProfile())
        }

    }
}