package com.example.yukgym

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var inputUsername: TextInputLayout
    private lateinit var inputPassword: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputUsername = findViewById(R.id.textInputLayout)
        inputPassword = findViewById(R.id.textInputLayout2)
        val btnLogin: Button = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener(View.OnClickListener {
            var checkLogin = false
            val username: String = inputUsername.getEditText()?.getText().toString()
            val password: String = inputPassword.getEditText()?.getText().toString()


            if (username.isEmpty()) {
                inputUsername.setError("Username must be filled with text")
                checkLogin = false
            }


            if(password.isEmpty()){
                inputPassword.setError("Password must be filled with text")
                checkLogin = false
            }


            if(username == "member" && password == "0000") checkLogin = true
            if(!checkLogin) return@OnClickListener
            val moveHome = Intent( this@MainActivity, HomeActivity::class.java)
            startActivity(moveHome)

        })

        tvRegister.setOnClickListener{
            startActivity<Register>()
        }
    }

}