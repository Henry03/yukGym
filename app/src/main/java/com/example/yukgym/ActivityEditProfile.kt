package com.example.yukgym

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.yukgym.databinding.ActivityEditProfileBinding
import com.example.yukgym.room.Register
import com.example.yukgym.room.RegisterDB
import com.google.android.material.snackbar.Snackbar
import java.util.*


class ActivityEditProfile : AppCompatActivity() {
    val db by lazy { RegisterDB(this) }
    var itemBinding: ActivityEditProfileBinding? = null
    var sharedPreferences: SharedPreferences? = null
    private lateinit var editProfileLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemBinding = ActivityEditProfileBinding.inflate(layoutInflater)

        setContentView(itemBinding?.root)

        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString("id", "")
        itemBinding?.etName?.setText(db?.registerDao()?.getRegister(id!!.toInt())?.name)
        itemBinding?.etEmail?.setText(db?.registerDao()?.getRegister(id!!.toInt())?.email)
        itemBinding?.etPhoneNumber?.setText(db?.registerDao()?.getRegister(id!!.toInt())?.notelp)
        itemBinding?.etBirthDate?.setText(db?.registerDao()?.getRegister(id!!.toInt())?.birthdate)

        itemBinding?.etBirthDate?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    // Display Selected date in textbox
                    itemBinding?.ilBirthDate?.editText?.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year)

                },
                year,
                month,
                day
            )

            dpd.show()
        }


        itemBinding?.btnSave?.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, ActivityLogin::class.java)

            val Name: String = itemBinding?.ilName?.editText?.getText().toString()
            val NoTelp: String = itemBinding?.ilPhoneNumber?.editText?.getText().toString()
            val Email: String = itemBinding?.ilEmail?.editText?.getText().toString()
            val BirthDate: String = itemBinding?.etBirthDate?.getText().toString()

            var checkSave = true

            if (Name.isEmpty()) {
                itemBinding?.ilName?.setError("Name must be filled with text")
                checkSave = false
            }

            if (NoTelp.isEmpty()) {
                itemBinding?.ilPhoneNumber?.setError("Phone Number must be filled with text")
                checkSave = false
            }

            if (Email.isEmpty()) {
                itemBinding?.ilEmail?.setError("E-mail must be filled with text")
                checkSave = false
            }

            if (!Email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
                itemBinding?.ilEmail?.setError("Email tidak valid")
                checkSave = false
            }

            if (BirthDate.isEmpty()) {
                itemBinding?.etEmail?.setError("Birth Date must be filled with text")
                checkSave = false
            }

            if (checkSave == true) {
                setupListener()
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

}