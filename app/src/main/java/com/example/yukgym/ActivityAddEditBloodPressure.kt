package com.example.yukgym

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.yukgym.databinding.ActivityAddEditBloodPressureBinding
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import com.example.yukgym.databinding.ActivityAddEditHistoryBinding
import com.example.yukgym.volley.api.BloodPressureApi
import com.example.yukgym.volley.api.HistoryApi
import com.example.yukgym.volley.models.BloodPressure
import com.example.yukgym.volley.models.History
import kotlinx.android.synthetic.main.activity_add_edit_blood_pressure.*
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

class ActivityAddEditBloodPressure : AppCompatActivity() {
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

    private var etSystolic: EditText? = null
    private var etDiastolic: EditText? = null
    private var etDatetime: EditText? = null
    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    var itemBinding: ActivityAddEditBloodPressureBinding? = null
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemBinding = ActivityAddEditBloodPressureBinding.inflate(layoutInflater)
        setContentView(itemBinding?.root)

        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val idUser = sharedPreferences?.getString("id", "1")
        val token = sharedPreferences?.getString("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNzlkMGU5MDU0NTYyYmRhYjhjOTIyZDVkOWEwYWYxODMyMjVkZmZmZjNlOWUzNGRmY2Y0MzQzOGMxMjNiYzYzMTgxMDk5MTlmNjMxMmFlZTMiLCJpYXQiOjE2NjkyNjgyMzUuMDM2MjE1LCJuYmYiOjE2NjkyNjgyMzUuMDM2MjIxLCJleHAiOjE3MDA4MDQyMzUuMDI0Nzg0LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.PPGgILqfx88hUIAGmLP0xKTO78umt0-99jOvV7qlIy0ayxnpngqspuuMcKhMbWZPls1o6SGvqaacLc8NgoFt9nlewIpd0VwlTkFTxja5v5fLoatJBnMrNR1dTUxQvK7DXpBHIt_9zEHUfX2Muj9H25OWKRV1iNgRFQFWeGDJVUv0lFn0HubHrsGBLHyMjPbDWWn6PiZ2V7qrxw-lgvU_Ml2BzpaKoVabhXOqKJClmQmUMb57C1DBl4uZ05-n675ShDkBY7zidxmOgEJQJDAGCAxO_B5G8SC6ziFAByjyUbET_x6AhZunvxOU3hSlkTLNmJqM9OAf2esMjg855SVxefFryMDZBwl3IkGV2oj1vOboARWeLYgwvs2SpOIt2yEipxdXlxYyttNnFPXTUEFm2KKvnZwHbSMmkkBUXg9pAosuCUyVOkoYkvvF8HWsz3u-ZtgbwhhRw35FNclu4ILfrtl-letZ4lDgZP8e7fmd3cJ0hxzma2H0tVXylIXjYiEuQEjFEQI42NV3tf0eKu8EX1yguaHY1AZfnfS432BVMvszrLIkpl5mVVkINxjNCYFlFxYnnXgJjl4iraUd4rpfzUlduVz_YiTzTyxjAwFJOSc2-QS_nc05eh99RE0ca_D1ILCf_ABimONtVg2AGu-futQHGiZXN0mRKm1KOGZQMhY")

        queue = Volley.newRequestQueue(this)
        etDatetime = itemBinding?.etDatetime
        etSystolic = itemBinding?.etSystolic
        etDiastolic = itemBinding?.etDiastolic
        layoutLoading = findViewById(R.id.layout_loading)


//        itemBinding?.etDatetime?.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val second = c.get(Calendar.SECOND)
            var time:String = ""

        itemBinding?.etDatetime?.setText("" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second)

        val btnCancel = itemBinding?.btnCancel
        btnCancel!!.setOnClickListener { finish() }

        val btnSave = itemBinding?.btnSave
        val tvTitle = itemBinding?.tvTitle
        val id = intent.getLongExtra("id", -1)
        println("idUser : " + idUser)
        if(id== -1L){
            tvTitle!!.setText("Add Blood Pressure Record")
            btnSave!!.setOnClickListener { createHistory(idUser!!.toLong(), token!!) }
        }else{
            println("id" +id)
            tvTitle!!.setText("Edit Blood Pressure Record")
            getHistoryById(id, token!!)

            btnSave!!.setOnClickListener { updateBloodPressure(id, token!!) }
        }
    }

    private fun getHistoryById(id: Long, token : String){
        setLoading(true)

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, BloodPressureApi.GET_BY_ID_URL + id,
                { response ->
                    val jsonObject = JSONObject(response)
                    val bloodPressure = Gson().fromJson(jsonObject.getString("data"), BloodPressure::class.java)
                    etSystolic!!.setText(bloodPressure.systolic.toString())
                    etDiastolic!!.setText(bloodPressure.diastolic.toString())
                    etDatetime!!.setText(bloodPressure.date_time)

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
                        Toast.makeText(this@ActivityAddEditBloodPressure, e.message, Toast.LENGTH_SHORT).show()

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

    private fun createHistory(id: Long, token : String){
        setLoading(true)
        
//        if(etDatetime!!.text.toString().isEmpty()) {
//            Toast.makeText(this@ActivityAddEditBloodPressure, "Berat Badan tidak boleh kosong!", Toast.LENGTH_SHORT)
//                .show()
//        }else if(etDiastolic!!.text.toString().isEmpty()) {
//            Toast.makeText( this@ActivityAddEditBloodPressure, "Aktivitas tidak boleh kosong!", Toast.LENGTH_SHORT).show()
//        }
//        else if(etSystolic!!.text.toString().isEmpty()) {
//            Toast.makeText( this@ActivityAddEditBloodPressure, "Lama Latihan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
//        }
//        else{

        val bloodPressure = BloodPressure(
            etDatetime!!.text.toString(),
            etSystolic!!.text.toString().toIntOrNull(),
            etDiastolic!!.text.toString().toIntOrNull(),
        )
        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, BloodPressureApi.ADD_URL, Response.Listener { response ->
                val gson = Gson()
                var bloodPressure = gson.fromJson(response, BloodPressure::class.java)

                if(bloodPressure != null) {
                    Toast.makeText(this@ActivityAddEditBloodPressure, "History Added", Toast.LENGTH_SHORT)
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
//                    val BeratBadan = itemBinding?.layoutBeratBadan
//                    val Aktivitas = itemBinding?.layoutAktivitas
//                    val LamaLatihan = itemBinding?.layoutLamaLatihan
//                    val Tanggal = itemBinding?.layoutTanggal
//
//                    if(errors.getString("errors").contains("berat_badan")){
//                        BeratBadan?.setError(errors.getJSONObject("errors").getJSONArray("berat_badan").get(0).toString())
//                    }
//                    if(errors.getString("errors").contains("aktivitas")){
//                        Aktivitas?.setError(errors.getJSONObject("errors").getJSONArray("aktivitas").get(0).toString())
//                    }
//                    if(errors.getString("errors").contains("lama_latihan")){
//                        LamaLatihan?.setError(errors.getJSONObject("errors").getJSONArray("lama_latihan").get(0).toString())
//                    }
//                    if(errors.getString("errors").contains("tanggal")){
//                        Tanggal?.setError(errors.getJSONObject("errors").getJSONArray("tanggal").get(0).toString())
//                    }

                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@ActivityAddEditBloodPressure, e.message, Toast.LENGTH_SHORT).show()
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
                    val requestBody = gson.toJson(bloodPressure)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }

        queue!!.add(stringRequest)

//        }

        
    }

    private fun updateBloodPressure(id: Long, token: String){
        setLoading(true)
        println("id :" + id)
        val bloodPressure = BloodPressure(
            etDatetime!!.text.toString(),
            etSystolic!!.text.toString().toIntOrNull(),
            etDiastolic!!.text.toString().toIntOrNull(),
        )
        val stringRequest: StringRequest =
            object: StringRequest(Method.PUT, BloodPressureApi.UPDATE_URL + id, Response.Listener { response ->
                val gson = Gson()
                var bloodPressure = gson.fromJson(response, History::class.java)

                if(bloodPressure != null)
                    Toast.makeText(this@ActivityAddEditBloodPressure, "Data berhasil diubah", Toast.LENGTH_SHORT).show()

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
                    Toast.makeText(this@ActivityAddEditBloodPressure, e.message, Toast.LENGTH_SHORT).show()
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
                    val requestBody = gson.toJson(bloodPressure)
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