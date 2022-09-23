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
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.yukgym.room.Register
import com.example.yukgym.room.RegisterDB
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class ActivityLogin : AppCompatActivity() {
    val db by lazy{ RegisterDB(this) }
    private lateinit var inputUsername: TextInputLayout
    private lateinit var inputPassword: TextInputLayout
    private lateinit var mainLayout: ConstraintLayout

    private val id = "id"
    private var access = false
    var sharedPreferences:SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

            if (registerDB != null) {
                sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
                var editor = sharedPreferences?.edit()
                editor?.putString("id", registerDB.id.toString())
                editor?.commit()
                val moveMenu = Intent(this, ActivityHome::class.java)
                startActivity(moveMenu)
            } else {
                Snackbar.make(
                    mainLayout,
                    "Username or Password incorrect",
                    Snackbar.LENGTH_LONG
                ).show()
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
}