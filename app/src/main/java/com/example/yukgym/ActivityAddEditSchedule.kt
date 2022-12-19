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
        val idUser = sharedPreferences?.getString("id", "1")
        val token = sharedPreferences?.getString("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNzlkMGU5MDU0NTYyYmRhYjhjOTIyZDVkOWEwYWYxODMyMjVkZmZmZjNlOWUzNGRmY2Y0MzQzOGMxMjNiYzYzMTgxMDk5MTlmNjMxMmFlZTMiLCJpYXQiOjE2NjkyNjgyMzUuMDM2MjE1LCJuYmYiOjE2NjkyNjgyMzUuMDM2MjIxLCJleHAiOjE3MDA4MDQyMzUuMDI0Nzg0LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.PPGgILqfx88hUIAGmLP0xKTO78umt0-99jOvV7qlIy0ayxnpngqspuuMcKhMbWZPls1o6SGvqaacLc8NgoFt9nlewIpd0VwlTkFTxja5v5fLoatJBnMrNR1dTUxQvK7DXpBHIt_9zEHUfX2Muj9H25OWKRV1iNgRFQFWeGDJVUv0lFn0HubHrsGBLHyMjPbDWWn6PiZ2V7qrxw-lgvU_Ml2BzpaKoVabhXOqKJClmQmUMb57C1DBl4uZ05-n675ShDkBY7zidxmOgEJQJDAGCAxO_B5G8SC6ziFAByjyUbET_x6AhZunvxOU3hSlkTLNmJqM9OAf2esMjg855SVxefFryMDZBwl3IkGV2oj1vOboARWeLYgwvs2SpOIt2yEipxdXlxYyttNnFPXTUEFm2KKvnZwHbSMmkkBUXg9pAosuCUyVOkoYkvvF8HWsz3u-ZtgbwhhRw35FNclu4ILfrtl-letZ4lDgZP8e7fmd3cJ0hxzma2H0tVXylIXjYiEuQEjFEQI42NV3tf0eKu8EX1yguaHY1AZfnfS432BVMvszrLIkpl5mVVkINxjNCYFlFxYnnXgJjl4iraUd4rpfzUlduVz_YiTzTyxjAwFJOSc2-QS_nc05eh99RE0ca_D1ILCf_ABimONtVg2AGu-futQHGiZXN0mRKm1KOGZQMhY")

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