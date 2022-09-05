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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Register : AppCompatActivity() {

    private lateinit var name: TextInputLayout
    private lateinit var nik: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var birthDate: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var passwordConfirm: TextInputLayout
    private lateinit var btnRegister: Button
    private lateinit var btnClear: Button
    private lateinit var signUpLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setTitle("Login")
        setupHyperlink()

        name = findViewById(R.id.ilName)
        nik = findViewById(R.id.ilNIK)
        email = findViewById(R.id.ilEmail)
        birthDate = findViewById(R.id.ilBirthDate)
        password = findViewById(R.id.ilPassword)
        passwordConfirm = findViewById(R.id.ilPasswordConfirm)
        btnRegister = findViewById(R.id.btnSignUp)
        btnClear = findViewById(R.id.btnClear)
        signUpLayout = findViewById(R.id.signUpLayout)

        btnClear.setOnClickListener {
            name.editText?.setText("")
            nik.editText?.setText("")
            email.editText?.setText("")
            birthDate.editText?.setText("")
            password.editText?.setText("")
            passwordConfirm.editText?.setText("")

            Snackbar.make(signUpLayout, "Text Cleared Success", Snackbar.LENGTH_LONG).show()
        }

        btnRegister.setOnClickListener(View.OnClickListener {
            val Name: String = name.editText?.getText().toString()
            val Nik: String = nik.editText?.getText().toString()
            val Email: String = email.editText?.getText().toString()
            val BirthDate: String = birthDate.editText?.getText().toString()
            val Password: String = password.editText?.getText().toString()
            val PasswordConfirm: String = passwordConfirm.editText?.getText().toString()

            var checkSignUp = true

            if(Name.isEmpty()){
                name.setError("Name must be filled with text")
                checkSignUp = false
            }

            if(Nik.isEmpty()){
                nik.setError("NIK must be filled with text")
                checkSignUp = false
            }

            if(Email.isEmpty()){
                email.setError("E-mail must be filled with text")
                checkSignUp = false
            }

            if(BirthDate.isEmpty()){
                birthDate.setError("Birth Date must be filled with text")
                checkSignUp = false
            }

            if(Password.isEmpty()){
                password.setError("Password must be filled with text")
                checkSignUp = false
            }

            if(PasswordConfirm.isEmpty()){
                passwordConfirm.setError("Password Confirmation must be filled with text")
                checkSignUp = false
            }

            if(Password != PasswordConfirm){
                passwordConfirm.setError("Password Confirmation doesn't match with password")
                checkSignUp = false
            }
            if(Nik.length != 16) {
                nik.setError("NIK length must be 16 digit")
            }

            if(checkSignUp == true){
                val movetoLogin = Intent(this, MainActivity::class.java)
                startActivity(movetoLogin)
            }else{
                return@OnClickListener
            }


        })

    }

    fun setupHyperlink(){
        val linkTextView = findViewById<TextView>(R.id.btnLogin)
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance())

        linkTextView.setOnClickListener(View.OnClickListener {
            val movetoLogin = Intent(this, MainActivity::class.java)
            startActivity(movetoLogin)
        })
    }
}