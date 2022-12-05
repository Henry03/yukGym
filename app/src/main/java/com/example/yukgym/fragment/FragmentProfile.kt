package com.example.yukgym.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.ActivityEditProfile
import com.example.yukgym.ActivityHome
import com.example.yukgym.ActivityLogin
import com.example.yukgym.R
import com.example.yukgym.room.RegisterDB
import com.example.yukgym.volley.api.ProfileApi
import com.example.yukgym.volley.models.Profile
import com.google.gson.Gson
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment
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
        val btnLogout : Button = view.findViewById(R.id.btnLogout)
        val btnAboutApp  = view.findViewById<CardView>(R.id.aboutApp)
        val fingerprintSwitch : Switch = view.findViewById(R.id.fingerprintSwitch)

        queue = Volley.newRequestQueue(requireContext())
        getHistoryById(id!!.toLong(), token!!, nameTxt, emailTxt, fingerprintSwitch)

        btnEdit.setOnClickListener(){
            (activity as ActivityHome).setActivity(ActivityEditProfile())
        }

        btnLogout.setOnClickListener(){
            val dialog = MaterialDialog.Builder(requireActivity()).setTitle("Logout")
                .setMessage("Are you sure you want to logout?").setCancelable(false)
                .setAnimation(R.raw.logout).setPositiveButton("Yes") { _, _ ->
                    logout(token!!)
                    (activity as ActivityHome).setActivity(ActivityLogin())

                }.setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.build()
            dialog.show()

            val animationView: LottieAnimationView = dialog.animationView

            //scale animation
            animationView.scaleX = 0.5f
            animationView.scaleY = 0.5f
        }

        btnAboutApp.setOnClickListener(){
            val dialog = MaterialDialog.Builder(requireActivity()).setTitle("About App")
                .setMessage("YukGym is an app that facilitate gym users.\n" +
                        "\nV1.0.1 Beta", TextAlignment.CENTER).setCancelable(false)
                .setAnimation(R.raw.information).setPositiveButton("I Understand") { dialogInterface, _ -> dialogInterface.dismiss()
                }.build()
            dialog.show()

            val animationView: LottieAnimationView = dialog.animationView

            //scale animation
            animationView.scaleX = 0.5f
            animationView.scaleY = 0.5f
        }

        var status = "0"
        fingerprintSwitch.setOnCheckedChangeListener(){ _, isChecked ->
            if(isChecked){
                status = "1"
            }else{
                status = "0"
            }
            fingerprintStatusUpdate(id!!.toLong(), token!!, status)
        }
    }

    private fun getHistoryById(id: Long, token:String, name:TextView, email :TextView, fingerprint: Switch) {
        sharedPreferences = this.getActivity()?.getSharedPreferences("login", Context.MODE_PRIVATE)
        println("id" + id)

        val stringRequest: StringRequest = object :
            StringRequest(
                Method.GET, ProfileApi.GET_BY_ID_URL + id,
                { response ->
                    val jsonObject = JSONObject(response)
                    val Username = jsonObject.getJSONObject("data").getString("username")
                    val Email = jsonObject.getJSONObject("data").getString("email")
                    val fingerprintStatus = jsonObject.getJSONObject("data").getString("fingerprint")
                    name.setText(Username)
                    email.setText(Email)
                    if(fingerprintStatus == "1"){
                        fingerprint.isChecked = true
                    }else{
                        fingerprint.isChecked = false
                    }
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

    private fun fingerprintStatusUpdate(id: Long, token:String, status: String) {
        sharedPreferences = this.getActivity()?.getSharedPreferences("login", Context.MODE_PRIVATE)


        val profile = Profile(
            "",
            "",
            "",
            "",
            "",
            "",
            status.toInt(),
            "null"
        )
        val stringRequest: StringRequest =
            object: StringRequest(Method.PUT, ProfileApi.FINGERPRINT + id, Response.Listener { response ->
                val gson = Gson()
                var history = gson.fromJson(response, Profile::class.java)

                if(history != null)
                    Toast.makeText(requireContext(), "Data berhasil diubah", Toast.LENGTH_SHORT).show()

            }, Response.ErrorListener { error ->
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        requireContext(),
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }){
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = java.util.HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(profile)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
        queue!!.add(stringRequest)
    }

    private fun logout(token : String) {
        val stringRequest: StringRequest =
            object :
                StringRequest(Method.POST, ProfileApi.LOGOUT, Response.Listener { response ->
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    var status = jsonObject.getString("message")

                    if (status != "Logout Succes")
                        Toast.makeText(requireContext(), "Logout Successfully", Toast.LENGTH_SHORT)
                            .show()

                }, Response.ErrorListener { error ->
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            requireContext(),
                            errors.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
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