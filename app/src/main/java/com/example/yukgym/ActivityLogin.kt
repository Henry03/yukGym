package com.example.yukgym

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.room.Register
import com.example.yukgym.room.RegisterDB
import com.example.yukgym.volley.api.ProfileApi
import com.example.yukgym.volley.models.Profile
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class ActivityLogin : AppCompatActivity() {
    val db by lazy{ RegisterDB(this) }
    private lateinit var inputUsername: TextInputLayout
    private lateinit var inputPassword: TextInputLayout
    private lateinit var mainLayout: ConstraintLayout

    private val id = "id"
    private var access = false
    private var queue: RequestQueue? = null
    var sharedPreferences:SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        queue = Volley.newRequestQueue(this)

        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "")
        println(token!!)
        authCheck(token!!)

        setupHyperlink()


        inputUsername = findViewById(R.id.ilUsername)
        inputPassword = findViewById(R.id.ilPassword)
        mainLayout = findViewById(R.id.mainLayout)

        val btnClear: Button
        val btnLogin: Button

        btnClear = findViewById(R.id.btnClear)
        btnLogin = findViewById(R.id.btnLogin)


        btnClear.setOnClickListener {
            inputUsername.editText?.setText("")
            inputPassword.editText?.setText("")

            Snackbar.make(mainLayout, "Text Cleared Success", Snackbar.LENGTH_LONG).show()
        }

        btnLogin.setOnClickListener(View.OnClickListener {

            val username: String = inputUsername.editText?.getText().toString()
            val password: String = inputPassword.editText?.getText().toString()

            if(username.isEmpty()){
                inputUsername.setError("Username must be filled with text")
            }

            if(password.isEmpty()){
                inputPassword.setError("Password must be filled with text")
            }

            inputUsername = findViewById(R.id.ilUsername)
            inputPassword = findViewById(R.id.ilPassword)
            val registerDB: Register = db.registerDao().getRegister(inputUsername.editText?.getText().toString(), inputPassword.editText?.getText().toString())
            loginProfile(inputUsername.editText?.text.toString(), inputPassword.editText?.text.toString())
//            if (registerDB != null) {
//                sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
//                var editor = sharedPreferences?.edit()
//                editor?.putString("id", registerDB.id.toString())
//                editor?.commit()
//                val moveMenu = Intent(this, ActivityHome::class.java)
//                startActivity(moveMenu)
//            } else {
//                Snackbar.make(
//                    mainLayout,
//                    "Username or Password incorrect",
//                    Snackbar.LENGTH_LONG
//                ).show()
//                return@OnClickListener
//            }
        })
    }

    fun setupHyperlink(){
        val linkTextView = findViewById<TextView>(R.id.btnSignUp)
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance())

        linkTextView.setOnClickListener(View.OnClickListener {
            val movetoActivityRegister = Intent(this, ActivityRegister::class.java)
            startActivity(movetoActivityRegister)
        })
    }

    private fun loginProfile(username: String, password: String){

        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, ProfileApi.LOGIN, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val id = jsonObject.getJSONObject("user").getString("id")


                var editor = sharedPreferences?.edit()
                editor?.putString("token", "null")
                editor?.commit()

                val token : String = jsonObject.getString("access_token")

                if(token != "null") {
                    Toast.makeText(this@ActivityLogin, "Login Successfully", Toast.LENGTH_SHORT)
                        .show()
                    sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
                    var editor = sharedPreferences?.edit()
                    editor?.putString("id",id)
                    editor?.putString("token", token)
                    editor?.commit()
                    val moveMenu = Intent(this, ActivityHome::class.java)
                    startActivity(moveMenu)
                }
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

            }, Response.ErrorListener { error ->
//                AlertDialog.Builder(applicationContext)
//                    .setTitle("Error")
//                    .setMessage(error.message)
//                    .setPositiveButton("OK", null)
//                    .show()
                    Toast.makeText(this@ActivityLogin, "Username/Password incorrect", Toast.LENGTH_SHORT)
                    .show()
            }){
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    println(headers)
                    return headers
                }

//                @Throws(AuthFailureError::class)
//                override fun getBody(): ByteArray {
//                    val gson = Gson()`
//                    val requestBody = gson.toJson(profile)
//                    return requestBody.toByteArray(StandardCharsets.UTF_8)
//                }
                override fun getParams(): MutableMap<String, String>?{
                    val params = HashMap<String, String>()
                    params["username"] = username
                    params["password"] = password
                    return params
                }

//                override fun getBodyContentType(): String {
//                    return "application/json"
//                }
            }
        queue!!.add(stringRequest)
    }

    private fun authCheck(token: String){
        val stringRequest : StringRequest = object:
            StringRequest(Method.POST, ProfileApi.AUTH, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val auth : String = jsonObject.getString("message").toString()
                println(token)
                if(auth == "Authenticated") {
                    sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
                    val moveMenu = Intent(this, ActivityHome::class.java)
                    startActivity(moveMenu)
                }
            }, Response.ErrorListener { error ->
//                AlertDialog.Builder(this@ActivityLogin)
//                    .setTitle("Error")
//                    .setMessage(error.message)
//                    .setPositiveButton("OK", null)
//                    .show()
//                try {
//                    val responseBody =
//                        String(error.networkResponse.data, StandardCharsets.UTF_8)
//                    val errors = JSONObject(responseBody)
//                    Toast.makeText(this@ActivityLogin, errors.getString("message"), Toast.LENGTH_SHORT).show()
//                } catch (e: Exception){
//                    Toast.makeText(this@ActivityLogin, e.message, Toast.LENGTH_SHORT).show()
//                }
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json";
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        queue!!.add(stringRequest)
    }

}

