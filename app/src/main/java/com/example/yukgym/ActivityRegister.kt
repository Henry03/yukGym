package com.example.yukgym

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.yukgym.databinding.ActivityRegisterBinding
import com.example.yukgym.room.Register
import com.example.yukgym.room.RegisterDB
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ActivityRegister : AppCompatActivity() {
    val db by lazy { RegisterDB(this) }
    var itemBinding : ActivityRegisterBinding? = null
    private lateinit var signUpLayout: ConstraintLayout
    private var registerId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemBinding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(itemBinding?.root)

        itemBinding?.etBirthDate?.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                itemBinding?.etBirthDate?.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year)

            }, year, month, day)

            dpd.show()
        }

        itemBinding?.btnClear?.setOnClickListener {
            itemBinding?.ilName?.editText?.setText("")
            itemBinding?.ilNoTelp?.editText?.setText("")
            itemBinding?.ilEmail?.editText?.setText("")
            itemBinding?.etBirthDate?.setText("")
            itemBinding?.ilPassword?.editText?.setText("")
            itemBinding?.ilPasswordConfirm?.editText?.setText("")

            Snackbar.make(signUpLayout, "Text Cleared Success", Snackbar.LENGTH_LONG).show()
        }

        itemBinding?.btnSignUp?.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, ActivityLogin::class.java)

            val Name: String = itemBinding?.ilName?.editText?.getText().toString()
            val NoTelp: String = itemBinding?.ilNoTelp?.editText?.getText().toString()
            val Email: String = itemBinding?.ilEmail?.editText?.getText().toString()
            val BirthDate: String = itemBinding?.etBirthDate?.getText().toString()
            val Password: String = itemBinding?.ilPassword?.editText?.getText().toString()
            val PasswordConfirm: String = itemBinding?.ilPasswordConfirm?.editText?.getText().toString()

            var checkSignUp = true

            if(Name.isEmpty()){
                itemBinding?.ilName?.setError("Name must be filled with text")
                checkSignUp = false
            }

            if(NoTelp.isEmpty()){
                itemBinding?.ilNoTelp?.setError("Phone Number must be filled with text")
                checkSignUp = false
            }

            if(Email.isEmpty()){
                itemBinding?.ilEmail?.setError("E-mail must be filled with text")
                checkSignUp = false
            }

            if (!Email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
                itemBinding?.ilEmail?.setError("Email tidak valid")
                checkSignUp = false
            }

            if(BirthDate.isEmpty()){
                itemBinding?.etEmail?.setError("Birth Date must be filled with text")
                checkSignUp = false
            }

            if(Password.isEmpty()){
                itemBinding?.etPassword?.setError("Password must be filled with text")
                checkSignUp = false
            }

            if(PasswordConfirm.isEmpty()){
                itemBinding?.etPasswordConfirm?.setError("Password Confirmation must be filled with text")
                checkSignUp = false
            }

            if(Password != PasswordConfirm){
                itemBinding?.etPasswordConfirm?.setError("Password Confirmation doesn't match with password")
                checkSignUp = false
            }

            if(checkSignUp == true){
                setupListener()
                Toast.makeText(applicationContext, itemBinding?.etName?.getText().toString() + " registered", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }else{
                return@OnClickListener
            }
        })

        itemBinding?.btnLogin?.setOnClickListener(View.OnClickListener {
            val movetoLogin = Intent(this, ActivityLogin::class.java)
            startActivity(movetoLogin)
        })
    }


    private fun setupListener() {
        db.registerDao().addRegister(
            Register(0, itemBinding?.etName?.getText().toString(), itemBinding?.etNoTelp?.text.toString(), itemBinding?.etEmail?.text.toString(), itemBinding?.etBirthDate?.text.toString(), itemBinding?.etPassword?.text.toString())
        )
        finish()
    }
}