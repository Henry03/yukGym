package com.example.yukgym

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.databinding.ActivityRegisterBinding
import com.example.yukgym.volley.api.ProfileApi
import com.example.yukgym.volley.models.Profile
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.nio.file.Files.list
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Collections.list
import kotlin.collections.HashMap

class ActivityRegister : AppCompatActivity() {

    var itemBinding : ActivityRegisterBinding? = null
    private lateinit var signUpLayout: ConstraintLayout
    private var registerId : Int = 0

    private val notificationId = 101
    private val CHANNEL_ID = "channel_notification"

    private var queue: RequestQueue? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemBinding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(itemBinding?.root)

        queue = Volley.newRequestQueue(this)
        signUpLayout = itemBinding!!.signUpLayout

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
            println(NoTelp)
            println(Email)
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
                createProfile()
                createNotificationChannel()
                sendNotification()
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

    private fun createProfile(){
        val formatter = DateTimeFormatter.ofPattern("d/MM/yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(itemBinding?.ilBirthDate?.editText?.getText().toString(), formatter)

        val profile = Profile(
            itemBinding?.ilName?.editText?.getText().toString(),
            itemBinding?.ilNoTelp?.editText?.getText().toString(),
            itemBinding?.ilEmail?.editText?.getText().toString(),
            date.toString(),
            itemBinding?.ilPassword?.editText?.getText().toString(),
            0
        )

        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, ProfileApi.ADD_URL, Response.Listener { response ->
                val gson = Gson()
                var profile = gson.fromJson(response,Profile::class.java)
                println(gson)
                if(profile != null)
                    Toast.makeText(this@ActivityRegister, "Register Successfully", Toast.LENGTH_SHORT).show()

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

            }, Response.ErrorListener { error ->
                AlertDialog.Builder(this@ActivityRegister)
                    .setTitle("Error")
                    .setMessage(error.message)
                    .setPositiveButton("OK", null)
                    .show()
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e: Exception){
                    Toast.makeText(this@ActivityRegister, e.message, Toast.LENGTH_SHORT).show()
                }
            }){
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    println(headers)
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(profile)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }
//                override fun getParams(): Map<String, String> {
//                    val params = HashMap<String, String>()
//                    params["name"] = itemBinding?.ilName?.editText?.getText().toString()
//                    params["notelp"] = itemBinding?.ilNoTelp?.editText?.getText().toString()
//                    params["email"] = itemBinding?.ilEmail?.editText?.getText().toString()
//                    params["birthdate"] = date.toString()
//                    params["password"] = itemBinding?.ilPassword?.editText?.getText().toString()
//                    return params
//                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
        queue!!.add(stringRequest)
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply{
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotification(){
        val intent: Intent = Intent(this, ActivityLogin::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val broadcastIntent: Intent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("toastMessage", "Login first to access the class")
        val actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.bodyattack_banner)
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(largeIcon)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_app)
            .setStyle(bigPictureStyle)
            .setContentTitle("Register Successful")
            .setContentText("Thank you for being our member. Let's try your first class here")
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(-551645)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "Access Class", actionIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        Toast.makeText(applicationContext, "Register with your new Account first", Toast.LENGTH_SHORT).show()
        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }


}
