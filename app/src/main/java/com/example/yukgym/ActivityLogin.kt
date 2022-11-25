package com.example.yukgym

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.hardware.fingerprint.FingerprintManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
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
import de.adorsys.android.finger.Finger
import de.adorsys.android.finger.FingerListener
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class ActivityLogin : AppCompatActivity(), FingerListener {
    val db by lazy{ RegisterDB(this) }
    private lateinit var inputUsername: TextInputLayout
    private lateinit var inputPassword: TextInputLayout
    private lateinit var mainLayout: ConstraintLayout

    private var access = false
    private var queue: RequestQueue? = null
    var sharedPreferences:SharedPreferences? = null

    private lateinit var finger: Finger
    private lateinit var fingerprintIcon: ImageView
    private var iconFingerprintEnabled: Drawable? = null
    private var iconFingerprintError: Drawable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        queue = Volley.newRequestQueue(this)


        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "")
        val id = sharedPreferences?.getString("id", "")
        println(token!!)
        authCheck(token!!)



        setupHyperlink()


        inputUsername = findViewById(R.id.ilUsername)
        inputPassword = findViewById(R.id.ilPassword)
        mainLayout = findViewById(R.id.mainLayout)

        val btnClear: Button
        val btnLogin: Button
        val btnFingerprint: CardView

        btnClear = findViewById(R.id.btnClear)
        btnLogin = findViewById(R.id.btnLogin)
        btnFingerprint = findViewById<CardView>(R.id.cvFingerprint)


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
        })

        btnFingerprint.setOnClickListener(View.OnClickListener {
            finger = Finger(
                this, mapOf(
                    Pair(FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE, "You don't have any suited hardware for using the fingerprint")
                )
            )

            fingerprintCheck(id!!.toLong())
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

    fun setFingerprint() {
        super.onResume()

        iconFingerprintEnabled = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fingerprint_24, theme)
        iconFingerprintError = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_fingerprint_24_fail, theme)

        finger.subscribe(this)

        val fingerprintsEnabled = finger.hasFingerprintEnrolled()
        val dialogs:Triple<String, String?, String?> = Triple(
            "Fingerprint Authentication",
            "Please scan your fingerprint to authenticate",
            "Please scan your fingerprint to authenticate"
        )

        fingerprintIcon = findViewById(R.id.ivFingerprint)
        fingerprintIcon.setImageDrawable(if (fingerprintsEnabled) iconFingerprintEnabled else iconFingerprintError)
        val showDialogButton = findViewById<CardView>(R.id.cvFingerprint)
        showDialogButton.setOnClickListener {
            finger.showDialog(
                this,
                dialogs
            )
        }

        if (!fingerprintsEnabled) {
            Toast.makeText(this, "You don't have any suited hardware for using the fingerprint", Toast.LENGTH_LONG).show()
        } else {
            finger.showDialog(
                this,
                dialogs
            )
        }
    }

    override fun onFingerprintAuthenticationSuccess() {
        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString("username", "")
        val password = sharedPreferences?.getString("password", "")
        println("hmm" + username + password)
        loginProfile(username!!, password!!)
        fingerprintIcon.setImageDrawable(iconFingerprintEnabled)
        finger.subscribe(this)
    }

    override fun onFingerprintAuthenticationFailure(errorMessage: String, errorCode: Int) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        fingerprintIcon.setImageDrawable(iconFingerprintError)
        finger.subscribe(this)
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
                    editor?.putString("username", username)
                    editor?.putString("password", password)
                    println("test" + username + password)
                    editor?.commit()
                    val moveMenu = Intent(this, ActivityHome::class.java)
                    startActivity(moveMenu)
                }
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

            }, Response.ErrorListener { error ->
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

                override fun getParams(): MutableMap<String, String>?{
                    val params = HashMap<String, String>()
                    params["username"] = username
                    params["password"] = password
                    return params
                }
            }
        queue!!.add(stringRequest)
    }

    private fun authCheck(token: String){
        val stringRequest : StringRequest = object:
            StringRequest(Method.POST, ProfileApi.AUTH, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val auth : String = jsonObject.getString("message").toString()
                if(auth == "Authenticated") {
                    sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
                    val moveMenu = Intent(this, ActivityHome::class.java)
                    startActivity(moveMenu)
                }
            }, Response.ErrorListener { error ->

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

    private fun fingerprintCheck(id: Long){
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, ProfileApi.FINGERPRINT_STATUS+id, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val auth : String = jsonObject.getString("data").toString()
                if(auth == "1") {
                    setFingerprint()
                }else{
                    Toast.makeText(this, "Fingerprint not set", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->

            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json";
                return headers
            }
        }
        queue!!.add(stringRequest)
    }
}

