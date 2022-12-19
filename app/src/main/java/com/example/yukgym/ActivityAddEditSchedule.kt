package com.example.yukgym

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import com.example.yukgym.databinding.ActivityEditScheduleBinding

import com.example.yukgym.volley.api.ScheduleApi
import com.example.yukgym.volley.models.Schedule
import java.util.*
import kotlin.collections.HashMap

class ActivityAddEditSchedule : AppCompatActivity() {
    companion object{
        private  val ACTIVITY_LIST = arrayOf(
            "Advanced Flow Yoga",
            "Aerial Flow Yoga",
            "Animal Flow",
            "Aqua Fit",
            "Aqua Zumba",
            "Body Attack",
            "Body Balance",
            "Body Combat",
            "Body Jam",
            "Body Pump"
        )
    }

    private var etTitle: EditText? = null
    private var etDate: EditText? = null
    private var edActivity: AutoCompleteTextView? = null
    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    var itemBinding: ActivityEditScheduleBinding? = null
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemBinding = ActivityEditScheduleBinding.inflate(layoutInflater)
        setContentView(itemBinding?.root)

        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val idUser = sharedPreferences?.getString("id", "9")
        val token = sharedPreferences?.getString("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiYzU4MWVhODFlNWU5NzNmYTE3YzNkNjgwYTUzMDI3NGU1OTU5N2Q5NTlkODdkYTZmMDYxNmU1NDJlYWMzMTcyYmY1Y2YzZDhhNzA1MGM1YjEiLCJpYXQiOjE2NzE0MjAzNjcuMjg3MywibmJmIjoxNjcxNDIwMzY3LjI4NzMwMywiZXhwIjoxNzAyOTU2MzY3LjI4MzM2Nywic3ViIjoiOSIsInNjb3BlcyI6W119.g6HcSpFhvj34Rk8A84KBrA27-p4RHJRsl6e3uxxljZ21nkj09QaWkBSJ-0MUap6H-I6HjI9rHDIWchTQThlr3Y2qfVp8kksm1idmezjoVoOQIdbaPRl6OIC4Go0iAeQRERuJRcAVzUvzrnPfYjyhMYsBfvnMWWDZ0HB4wJXd23mK7zYu9wum99mtFgJC7CujAsx8soTAg6PeWcYG8AbOf28yxpr06MLNkYLRgQzIiQBXDYBZis0d6GVxgu5SCjjNy2L-6fYQgZ7wO7TdX3P7mMFiMcbkh4-9aFaTSrAxLAdADhRm90cOj3W-QMQEMNIZnOoVWiLANwhhMUzcBuFEp5S_0_fDUZg_5YjFcuKv1dqBBeCBEeBqyV4vODUFS85ZptfpE5EXiqg52Y7MsThR3gW779nX05HmaCLO2I4Ti01I_jgv-6G2O7YTwXAkQkfm3U8ZTwBYRTq1_OFxy5zT9Yzt02LsRhSqNHrt-htCGtIbLQ8av-u3u7o3JwwakmChUVGJD7maac2n0VagK90IuQJkubozmgndAQilgsB6SEv6tu1c7aeHiTBmRX0tg852Mq70s-JPDqoX6cbkjEqq-kDAa0pgYG1qRaZ8RpIma1KZnpvxugOC0LM5AJGg508lb_G5c9fxYqNO61lr8kyYseuOV25pXv5wVbPB_ATqvRI")

        queue = Volley.newRequestQueue(this)
        edActivity = itemBinding?.etActivity
        etTitle = itemBinding?.etTitle
        etDate = itemBinding?.etDate
        layoutLoading = findViewById(R.id.layout_loading)

        setExposedDropdownMenu()

        itemBinding?.etDate?.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                itemBinding?.etDate?.setText("" + year + "-" + monthOfYear + "-" + dayOfMonth)

            }, year, month, day)

            dpd.show()
        }

        val btnCancel = itemBinding?.btnCancel
        btnCancel!!.setOnClickListener { finish() }

        val btnSave = itemBinding?.btnSave
        val tvTitle = itemBinding?.tvTitle
        val id = intent.getLongExtra("id", -1)
        println("idUser : " + idUser)
        if(id== -1L){
            tvTitle!!.setText("Tambah Schedule")
            btnSave!!.setOnClickListener { createSchedule(token!!) }
        }else{
            println("id" +id)
            tvTitle!!.setText("Edit Schedule")
            getScheduleById(id, token!!)

            btnSave!!.setOnClickListener { updateSchedule(id, token!!) }
        }
    }

    fun setExposedDropdownMenu(){
        val adapterSchedule: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.item_list_history, ACTIVITY_LIST)
        edActivity!!.setAdapter(adapterSchedule)
    }

    private fun getScheduleById(id: Long, token : String){
        setLoading(true)

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, ScheduleApi.GET_BY_ID_URL + id,
                { response ->
                    val jsonObject = JSONObject(response)
                    val schedule = Gson().fromJson(jsonObject.getString("data"), Schedule::class.java)
                    etTitle!!.setText(schedule.title)
                    etDate!!.setText(schedule.date)
                    edActivity!!.setText(schedule.activity)
                    setExposedDropdownMenu()
                    setLoading(false)
                },
                Response.ErrorListener{ error ->
                    setLoading(false)
                    try{
                        val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this,
                            errors.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception){
                        Toast.makeText(this@ActivityAddEditSchedule, e.message, Toast.LENGTH_SHORT).show()

                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        queue!!.add(stringRequest)

    }

    private fun createSchedule(token : String){
        setLoading(true)
        
//        if(etTitle!!.text.toString().isEmpty()) {
//            Toast.makeText(this@ActivityAddEditSchedule, "Berat Badan tidak boleh kosong!", Toast.LENGTH_SHORT)
//                .show()
//        }else if(edActivity!!.text.toString().isEmpty()) {
//            Toast.makeText( this@ActivityAddEditSchedule, "Aktivitas tidak boleh kosong!", Toast.LENGTH_SHORT).show()
//        }
//        else if(etDate!!.text.toString().isEmpty()) {
//            Toast.makeText( this@ActivityAddEditSchedule, "Tanggal tidak boleh kosong!", Toast.LENGTH_SHORT).show()
//        }else{

        val schedule = Schedule(
            etTitle!!.text.toString(),
            etDate!!.text.toString(),
            edActivity!!.text.toString(),
        )
        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, ScheduleApi.ADD_URL, Response.Listener { response ->
                val gson = Gson()
                var schedule = gson.fromJson(response, Schedule::class.java)

                if(schedule != null) {
                    Toast.makeText(this@ActivityAddEditSchedule, "Scheduele Added", Toast.LENGTH_SHORT)
                        .show()
                }

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

                setLoading(false)
            }, Response.ErrorListener { error ->
                setLoading(false)
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    val Title = itemBinding?.ilScheduleTitle
                    val Activity = itemBinding?.ilScheduleActivity
                    val Date = itemBinding?.ilScheduleDate

//                    if(errors.getString("errors").contains("title")){
//                        Title?.setError(errors.getJSONObject("errors").getJSONArray("title").get(0).toString())
//                    }
//                    if(errors.getString("errors").contains("activity")){
//                        Activity?.setError(errors.getJSONObject("errors").getJSONArray("activity").get(0).toString())
//                    }
//                    if(errors.getString("errors").contains("date")){
//                        Date?.setError(errors.getJSONObject("errors").getJSONArray("date").get(0).toString())
//                    }

                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@ActivityAddEditSchedule, e.message, Toast.LENGTH_SHORT).show()
                }
            }){
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(schedule)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }

        queue!!.add(stringRequest)

//        }

        
    }

    private fun updateSchedule(id: Long, token: String){
        println(id)
        setLoading(true)
        val schedule = Schedule(
            etTitle!!.text.toString(),
            etDate!!.text.toString(),
            edActivity!!.text.toString(),
        )
        val stringRequest: StringRequest =
            object: StringRequest(Method.PUT, ScheduleApi.UPDATE_URL + id, Response.Listener { response ->
                val gson = Gson()
                var schedule = gson.fromJson(response, Schedule::class.java)

                if(schedule != null)
                    Toast.makeText(this@ActivityAddEditSchedule, "Data berhasil diubah", Toast.LENGTH_SHORT).show()

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

                setLoading(false)
            }, Response.ErrorListener { error ->
                setLoading(false)
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@ActivityAddEditSchedule, e.message, Toast.LENGTH_SHORT).show()
                }
            }){
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(schedule)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
        queue!!.add(stringRequest)
    }

    private fun setLoading(isLoading: Boolean){
        if(isLoading){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            layoutLoading!!.visibility = View.INVISIBLE
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            layoutLoading!!.visibility = View.INVISIBLE
        }
    }
}