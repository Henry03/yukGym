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
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.room.RegisterDB
import com.example.yukgym.volley.api.ProfileApi
import com.example.yukgym.volley.models.Profile
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class FragmentProfile : Fragment() {
    val db by lazy{activity?.let { RegisterDB(it )}  }

    private var queue: RequestQueue? = null
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
        val nameTxt :TextView =  view.findViewById(R.id.tvname)
        val emailTxt :TextView =  view.findViewById(R.id.tvemail)
        sharedPreferences = this.getActivity()?.getSharedPreferences("login", Context.MODE_PRIVATE)
        val id = sharedPreferences!!.getString("id", "")
        val token = sharedPreferences!!.getString("token", "")
        val btnEdit : Button = view.findViewById(R.id.btnEdit)
        queue = Volley.newRequestQueue(requireContext())
        getMahasiswaById(id!!.toLong(), token!!, nameTxt, emailTxt)

        btnEdit.setOnClickListener(){
            (activity as ActivityHome).setActivity(ActivityEditProfile())
        }
    }

    private fun getMahasiswaById(id: Long, token:String, name:TextView, email :TextView){
        sharedPreferences = this.getActivity()?.getSharedPreferences("login", Context.MODE_PRIVATE)
        println("id" + id)

        val stringRequest: StringRequest = object :
            StringRequest(
                Method.GET, ProfileApi.GET_BY_ID_URL + id,
                { response ->
                    val jsonObject = JSONObject(response)
                    val Username = jsonObject.getJSONObject("data").getString("username")
                    val Email = jsonObject.getJSONObject("data").getString("email")
                    name.setText(Username)
                    email.setText(Email)
                },
                Response.ErrorListener{ error ->

                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        queue!!.add(stringRequest)
    }
}