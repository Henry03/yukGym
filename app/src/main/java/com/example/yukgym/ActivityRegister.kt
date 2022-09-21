package com.example.yukgym

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yukgym.databinding.ActivityRegisterBinding
import com.example.yukgym.room.Register
import com.google.android.gms.tasks.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

class ActivityRegister : AppCompatActivity() {
    private lateinit var itemBinding : ActivityRegisterBinding
    private lateinit var signUpLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        itemBinding = ActivityRegisterBinding.inflate(layoutInflater)

        itemBinding.etBirthDate.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                itemBinding.etBirthDate.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year)

            }, year, month, day)

            dpd.show()
        }

        itemBinding.btnClear.setOnClickListener {
            itemBinding.ilName.editText?.setText("")
            itemBinding.ilNoTelp.editText?.setText("")
            itemBinding.ilEmail.editText?.setText("")
            itemBinding.etBirthDate.setText("")
            itemBinding.ilPassword.editText?.setText("")
            itemBinding.ilPasswordConfirm.editText?.setText("")

            Snackbar.make(signUpLayout, "Text Cleared Success", Snackbar.LENGTH_LONG).show()
        }

        itemBinding.btnSignUp.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, ActivityLogin::class.java)
            val mbundle = Bundle()

            val Name: String = itemBinding.ilName.editText?.getText().toString()
            val NoTelp: String = itemBinding.ilNoTelp.editText?.getText().toString()
            val Email: String = itemBinding.ilEmail.editText?.getText().toString()
            val BirthDate: String = itemBinding.etBirthDate.getText().toString()
            val Password: String = itemBinding.ilPassword.editText?.getText().toString()
            val PasswordConfirm: String = itemBinding.ilPasswordConfirm.editText?.getText().toString()

            var checkSignUp = true

            if(Name.isEmpty()){
                itemBinding.ilName.setError("Name must be filled with text")
                checkSignUp = false
            }

            if(NoTelp.isEmpty()){
                itemBinding.ilNoTelp.setError("Phone Number must be filled with text")
                checkSignUp = false
            }

            if(Email.isEmpty()){
                itemBinding.ilEmail.setError("E-mail must be filled with text")
                checkSignUp = false
            }

            if (!Email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
                itemBinding.ilEmail.setError("Email tidak valid")
                checkSignUp = false
            }

            if(BirthDate.isEmpty()){
                itemBinding.etEmail.setError("Birth Date must be filled with text")
                checkSignUp = false
            }

            if(Password.isEmpty()){
                itemBinding.etPassword.setError("Password must be filled with text")
                checkSignUp = false
            }

            if(PasswordConfirm.isEmpty()){
                itemBinding.etPasswordConfirm.setError("Password Confirmation must be filled with text")
                checkSignUp = false
            }

            if(Password != PasswordConfirm){
                itemBinding.etPasswordConfirm.setError("Password Confirmation doesn't match with password")
                checkSignUp = false
            }
            if(NoTelp.length != 12) {
                itemBinding.etPasswordConfirm.setError("Phone Number length must be 12 digit")
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

        itemBinding.btnLogin.setOnClickListener(View.OnClickListener {
            val movetoLogin = Intent(this, ActivityLogin::class.java)
            startActivity(movetoLogin)
        })

    }
    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(arrayListOf(), object :
            NoteAdapter.OnAdapterListener {
            override fun onClick(note: Note) {
                //Toast.makeText(applicationContext, note.title,
//                Toast.LENGTH_SHORT).show()
                intentEdit(note.id, Constant.TYPE_READ)
            }

            override fun onUpdate(note: Note) {
                intentEdit(note.id, Constant.TYPE_UPDATE)
            }

            override fun onDelete(note: Note) {
                deleteDialog(note)
            }
        })
        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }
}