package com.example.yukgym

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Camera
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.databinding.ActivityEditProfileBinding
import com.example.yukgym.hardware.ActivityCamera
import com.example.yukgym.hardware.CameraView
import com.example.yukgym.room.Register
import com.example.yukgym.room.RegisterDB
import com.example.yukgym.volley.api.ProfileApi
import com.example.yukgym.volley.models.Profile
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*



class ActivityEditProfile : AppCompatActivity() {
    val db by lazy { RegisterDB(this) }
    var itemBinding: ActivityEditProfileBinding? = null
    var sharedPreferences: SharedPreferences? = null
    private var queue: RequestQueue? = null
    private lateinit var editProfileLayout: FrameLayout

    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        queue = Volley.newRequestQueue(applicationContext)
        setContentView(itemBinding?.root)

        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString("id", "")
        val token = sharedPreferences?.getString("token", "")
        getHistoryById(id!!.toLong(), token!!.toString())
//        itemBinding?.etName?.setText(db?.registerDao()?.getRegister(id!!.toInt())?.name)
//        itemBinding?.etEmail?.setText(db?.registerDao()?.getRegister(id!!.toInt())?.email)
//        itemBinding?.etPhoneNumber?.setText(db?.registerDao()?.getRegister(id!!.toInt())?.notelp)
//        itemBinding?.etBirthDate?.setText(db?.registerDao()?.getRegister(id!!.toInt())?.birthdate)

        itemBinding?.etBirthDate?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    itemBinding?.ilBirthDate?.editText?.setText("" + year + "-" + monthOfYear + "-" + dayOfMonth)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        itemBinding?.ivProfile?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ActivityCamera::class.java)

            startActivity(intent)
        })

        itemBinding?.btnSave?.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, ActivityLogin::class.java)

            val Name: String = itemBinding?.ilName?.editText?.getText().toString()
            val NoTelp: String = itemBinding?.ilPhoneNumber?.editText?.getText().toString()
            val Email: String = itemBinding?.ilEmail?.editText?.getText().toString()
            val BirthDate: String = itemBinding?.etBirthDate?.getText().toString()

            var checkSave = true

            if (Name.isEmpty()) {
//                itemBinding?.ilName?.setError("Name must be filled with text")
                checkSave = false
            }

            if (NoTelp.isEmpty()) {
//                itemBinding?.ilPhoneNumber?.setError("Phone Number must be filled with text")
                checkSave = false
            }

            if (Email.isEmpty()) {
//                itemBinding?.ilEmail?.setError("E-mail must be filled with text")
                checkSave = false
            }

            if (!Email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
//                itemBinding?.ilEmail?.setError("Email tidak valid")
                checkSave = false
            }

            if (BirthDate.isEmpty()) {
//                itemBinding?.etEmail?.setError("Birth Date must be filled with text")
                checkSave = false
            }

            updateProfile(id!!.toLong(), token!!.toString())
            if (checkSave == true) {

                Toast.makeText(
                    applicationContext,
                    "Your Profile Changed",
                    Toast.LENGTH_SHORT
                ).show()
                val moveMenu = Intent(this, ActivityHome::class.java)
                startActivity(moveMenu)
            } else {
                return@OnClickListener
            }
        })

    }

    private fun setupListener() {
        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString("id", "")

        db.registerDao().updateNote(
            Register(
                id!!.toInt(),
                itemBinding?.etName?.getText().toString(),
                itemBinding?.etPhoneNumber?.text.toString(),
                itemBinding?.etEmail?.text.toString(),
                itemBinding?.etBirthDate?.text.toString(),
                db?.registerDao()?.getRegister(id!!.toInt())?.password.toString()
            )
        )
        finish()
    }

    private fun getHistoryById(id: Long, token:String){
        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)

        val Username = itemBinding?.ilName?.editText
        val NoTelp = itemBinding?.ilPhoneNumber?.editText
        val Email = itemBinding?.ilEmail?.editText
        val BirthDate = itemBinding?.etBirthDate

        val stringRequest: StringRequest = object :
            StringRequest(
                Method.GET, ProfileApi.GET_BY_ID_URL + id,
                { response ->
                    val jsonObject = JSONObject(response)
                    val username = jsonObject.getJSONObject("data").getString("username")
                    val noTelp = jsonObject.getJSONObject("data").getString("notelp")
                    val email = jsonObject.getJSONObject("data").getString("email")
                    val birthdate = jsonObject.getJSONObject("data").getString("birthdate")

                    Username?.setText(username)
                    NoTelp?.setText(noTelp)
                    Email?.setText(email)
                    BirthDate?.setText(birthdate)

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

    private fun updateProfile(id: Long, token :String){
        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)

        val Username = itemBinding?.ilName?.editText
        val NoTelp = itemBinding?.ilPhoneNumber?.editText
        val Email = itemBinding?.ilEmail?.editText
        val BirthDate = itemBinding?.etBirthDate


        val profile = Profile(
            Username?.text.toString(),
            NoTelp?.text.toString(),
            Email?.text.toString(),
            BirthDate?.text.toString(),
            "1",
            "1",
            0,
            "null"
        )
        val stringRequest: StringRequest =
            object: StringRequest(Method.PUT, ProfileApi.UPDATE_URL + id, Response.Listener { response ->
                val gson = Gson()
                var history = gson.fromJson(response, Profile::class.java)

                if(history != null)
                    Toast.makeText(this@ActivityEditProfile, "Data berhasil diubah", Toast.LENGTH_SHORT).show()

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()
            }, Response.ErrorListener { error ->
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    val message = errors.getString("message")
                    println("profile" + errors.getString("errors"))

                    val Name = itemBinding?.ilName
                    val NoTelp = itemBinding?.ilPhoneNumber
                    val Email = itemBinding?.ilEmail
                    val BirthDate = itemBinding?.etBirthDate

                    if(errors.getString("errors").contains("username")){
                        Name?.setError(errors.getJSONObject("errors").getJSONArray("username").get(0).toString())
                    }
                    if(errors.getString("errors").contains("email")){
                        Email?.setError(errors.getJSONObject("errors").getJSONArray("email").get(0).toString())
                    }
                    if(errors.getString("errors").contains("notelp")){
                        NoTelp?.setError(errors.getJSONObject("errors").getJSONArray("notelp").get(0).toString())
                    }
                    if(errors.getString("errors").contains("birthdate")){
                        BirthDate?.setError(errors.getJSONObject("errors").getJSONArray("birthdate").get(0).toString())
                    }

                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@ActivityEditProfile, e.message, Toast.LENGTH_SHORT).show()
                }
            }){
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
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
}