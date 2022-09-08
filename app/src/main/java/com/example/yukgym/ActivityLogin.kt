package com.example.yukgym

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class ActivityLogin : AppCompatActivity() {
    private lateinit var inputUsername: TextInputLayout
    private lateinit var inputPassword: TextInputLayout
    private lateinit var mainLayout: ConstraintLayout

    var mBundle: Bundle? = null
    lateinit var Nama: String
    lateinit var Email: String
    lateinit var BirthDate: String
    lateinit var Password: String
    lateinit var NoTelp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTitle("Login")
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

        getBundle()

        btnLogin.setOnClickListener(View.OnClickListener {

            val username: String = inputUsername.editText?.getText().toString()
            val password: String = inputPassword.editText?.getText().toString()

            if(username.isEmpty()){
                inputUsername.setError("Username must be filled with text")
            }

            if(password.isEmpty()){
                inputPassword.setError("Password must be filled with text")
            }

            if(username == "admin" && password == "yukgym") {
                val moveMenu = Intent(this, ActivityHome::class.java)
                startActivity(moveMenu)
            }else if(mBundle == null) {
                Snackbar.make(mainLayout, "Register first", Snackbar.LENGTH_LONG).show()
            }else if(username == Nama && password == Password || username == "admin" && password == "yukgym"){
                val moveMenu = Intent(this, ActivityHome::class.java)
                startActivity(moveMenu)
            }else{
                Snackbar.make(mainLayout, "Username or Password incorrect", Snackbar.LENGTH_LONG).show()
                return@OnClickListener
            }



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

    fun getBundle(){
        //Mengambil bundle dari activity sebelumnya dengan menggunakan key register mBundle = intent.getBundleExtra("signup")!!
        if(intent.getBundleExtra("signup") != null) {
            mBundle = intent.getBundleExtra("signup")

            // Mengambil data dari bundle
            Nama = mBundle?.getString("name")!!
            Email = mBundle?.getString("email")!!
            BirthDate = mBundle?.getString("birthdate")!!
            Password = mBundle?.getString("password")!!
            NoTelp = mBundle?.getString("notelp")!!
        }


    }

}