package com.example.yukgym

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

class ActivityRegister : AppCompatActivity() {

    private lateinit var name: TextInputLayout
    private lateinit var noTelp: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var birthDate: EditText
    private lateinit var password: TextInputLayout
    private lateinit var passwordConfirm: TextInputLayout
    private lateinit var btnRegister: Button
    private lateinit var btnClear: Button
    private lateinit var signUpLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupHyperlink()

        name = findViewById(R.id.ilName)
        noTelp = findViewById(R.id.ilNoTelp)
        email = findViewById(R.id.ilEmail)
        birthDate = findViewById(R.id.etBirthDate)
        password = findViewById(R.id.ilPassword)
        passwordConfirm = findViewById(R.id.ilPasswordConfirm)
        btnRegister = findViewById(R.id.btnSignUp)
        btnClear = findViewById(R.id.btnClear)
        signUpLayout = findViewById(R.id.signUpLayout)

        birthDate.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                birthDate.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year)

            }, year, month, day)

            dpd.show()
        }

        btnClear.setOnClickListener {
            name.editText?.setText("")
            noTelp.editText?.setText("")
            email.editText?.setText("")
            birthDate.setText("")
            password.editText?.setText("")
            passwordConfirm.editText?.setText("")

            Snackbar.make(signUpLayout, "Text Cleared Success", Snackbar.LENGTH_LONG).show()
        }

        btnRegister.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, ActivityLogin::class.java)
            val mbundle = Bundle()

            val Name: String = name.editText?.getText().toString()
            val NoTelp: String = noTelp.editText?.getText().toString()
            val Email: String = email.editText?.getText().toString()
            val BirthDate: String = birthDate.getText().toString()
            val Password: String = password.editText?.getText().toString()
            val PasswordConfirm: String = passwordConfirm.editText?.getText().toString()

            var checkSignUp = true

            if(Name.isEmpty()){
                name.setError("Name must be filled with text")
                checkSignUp = false
            }

            if(NoTelp.isEmpty()){
                noTelp.setError("Phone Number must be filled with text")
                checkSignUp = false
            }

            if(Email.isEmpty()){
                email.setError("E-mail must be filled with text")
                checkSignUp = false
            }

            if (!Email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
                email.setError("Email tidak valid")
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
            if(NoTelp.length != 12) {
                noTelp.setError("Phone Number length must be 12 digit")
                checkSignUp = false
            }

            mbundle.putString("name", Name)
            mbundle.putString("notelp", NoTelp)
            mbundle.putString("email", Email)
            mbundle.putString("birthdate", BirthDate)
            mbundle.putString("password", Password)

            intent.putExtra("signup", mbundle)


            if(checkSignUp == true){
//                val movetoLogin = Intent(this, MainActivity::class.java)
//                startActivity(movetoLogin)
                startActivity(intent)
            }else{
                return@OnClickListener
            }


        })

    }

    fun setupHyperlink(){
        val linkTextView = findViewById<TextView>(R.id.btnLogin)
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance())

        linkTextView.setOnClickListener(View.OnClickListener {
            val movetoLogin = Intent(this, ActivityLogin::class.java)
            startActivity(movetoLogin)
        })

    }
}