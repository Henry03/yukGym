package com.example.yukgym

import android.app.DatePickerDialog
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
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import com.example.yukgym.databinding.ActivityAddEditHistoryBinding
import com.example.yukgym.volley.api.HistoryApi
import com.example.yukgym.volley.models.History
import java.util.*
import kotlin.collections.HashMap

class ActivityAddEditHistory : AppCompatActivity() {
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

    private var etBeratBadan: EditText? = null
    private var etLamaLatihan: EditText? = null
    private var etTanggal: EditText? = null
    private var edAktivitas: AutoCompleteTextView? = null
    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    var itemBinding: ActivityAddEditHistoryBinding? = null
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemBinding = ActivityAddEditHistoryBinding.inflate(layoutInflater)
        setContentView(itemBinding?.root)

        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val idUser = sharedPreferences?.getString("id", "9")
        val token = sharedPreferences?.getString("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiYzU4MWVhODFlNWU5NzNmYTE3YzNkNjgwYTUzMDI3NGU1OTU5N2Q5NTlkODdkYTZmMDYxNmU1NDJlYWMzMTcyYmY1Y2YzZDhhNzA1MGM1YjEiLCJpYXQiOjE2NzE0MjAzNjcuMjg3MywibmJmIjoxNjcxNDIwMzY3LjI4NzMwMywiZXhwIjoxNzAyOTU2MzY3LjI4MzM2Nywic3ViIjoiOSIsInNjb3BlcyI6W119.g6HcSpFhvj34Rk8A84KBrA27-p4RHJRsl6e3uxxljZ21nkj09QaWkBSJ-0MUap6H-I6HjI9rHDIWchTQThlr3Y2qfVp8kksm1idmezjoVoOQIdbaPRl6OIC4Go0iAeQRERuJRcAVzUvzrnPfYjyhMYsBfvnMWWDZ0HB4wJXd23mK7zYu9wum99mtFgJC7CujAsx8soTAg6PeWcYG8AbOf28yxpr06MLNkYLRgQzIiQBXDYBZis0d6GVxgu5SCjjNy2L-6fYQgZ7wO7TdX3P7mMFiMcbkh4-9aFaTSrAxLAdADhRm90cOj3W-QMQEMNIZnOoVWiLANwhhMUzcBuFEp5S_0_fDUZg_5YjFcuKv1dqBBeCBEeBqyV4vODUFS85ZptfpE5EXiqg52Y7MsThR3gW779nX05HmaCLO2I4Ti01I_jgv-6G2O7YTwXAkQkfm3U8ZTwBYRTq1_OFxy5zT9Yzt02LsRhSqNHrt-htCGtIbLQ8av-u3u7o3JwwakmChUVGJD7maac2n0VagK90IuQJkubozmgndAQilgsB6SEv6tu1c7aeHiTBmRX0tg852Mq70s-JPDqoX6cbkjEqq-kDAa0pgYG1qRaZ8RpIma1KZnpvxugOC0LM5AJGg508lb_G5c9fxYqNO61lr8kyYseuOV25pXv5wVbPB_ATqvRI")

        queue = Volley.newRequestQueue(this)
        edAktivitas = itemBinding?.etAktivitas
        etBeratBadan = itemBinding?.etBeratBadan
        etLamaLatihan = itemBinding?.etLamaLatihan
        etTanggal = itemBinding?.etTanggal
        layoutLoading = findViewById(R.id.layout_loading)

        setExposedDropdownMenu()

        itemBinding?.etTanggal?.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                itemBinding?.etTanggal?.setText("" + year + "-" + monthOfYear + "-" + dayOfMonth)

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
            tvTitle!!.setText("Tambah History")
            btnSave!!.setOnClickListener { createHistory(idUser!!.toLong(), token!!) }
        }else{
            println("id" +id)
            tvTitle!!.setText("Edit History")
            getHistoryById(id, token!!)

            btnSave!!.setOnClickListener { updateHistory(id, token!!) }
        }
    }

    fun setExposedDropdownMenu(){
        val adapterHistory: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.item_list_history, ACTIVITY_LIST)
        edAktivitas!!.setAdapter(adapterHistory)
    }

    private fun getHistoryById(id: Long, token : String){
        setLoading(true)

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, HistoryApi.GET_BY_ID_URL + id,
                { response ->
                    val jsonObject = JSONObject(response)
                    val history = Gson().fromJson(jsonObject.getString("data"), History::class.java)
                    println("beratbadan" + history.berat_badan)
                    etBeratBadan!!.setText(history.berat_badan.toString())
                    etLamaLatihan!!.setText(history.lama_latihan.toString())
                    etTanggal!!.setText(history.tanggal)
                    edAktivitas!!.setText(history.aktivitas)
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
                        Toast.makeText(this@ActivityAddEditHistory, e.message, Toast.LENGTH_SHORT).show()

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
        
//        if(etBeratBadan!!.text.toString().isEmpty()) {
//            Toast.makeText(this@ActivityAddEditHistory, "Berat Badan tidak boleh kosong!", Toast.LENGTH_SHORT)
//                .show()
//        }else if(edAktivitas!!.text.toString().isEmpty()) {
//            Toast.makeText( this@ActivityAddEditHistory, "Aktivitas tidak boleh kosong!", Toast.LENGTH_SHORT).show()
//        }
//        else if(etLamaLatihan!!.text.toString().isEmpty()) {
//            Toast.makeText( this@ActivityAddEditHistory, "Lama Latihan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
//        }
//        else if(etTanggal!!.text.toString().isEmpty()) {
//            Toast.makeText( this@ActivityAddEditHistory, "Tanggal tidak boleh kosong!", Toast.LENGTH_SHORT).show()
//        }else{

        val history = History(
            id,
            etBeratBadan!!.text.toString().toFloatOrNull(),
            edAktivitas!!.text.toString(),
            etLamaLatihan!!.text.toString().toIntOrNull(),
            etTanggal!!.text.toString(),
            "null"
        )
        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, HistoryApi.ADD_URL, Response.Listener { response ->
                val gson = Gson()
                var history = gson.fromJson(response, History::class.java)

                if(history != null) {
                    Toast.makeText(this@ActivityAddEditHistory, "History Added", Toast.LENGTH_SHORT)
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
                    val BeratBadan = itemBinding?.layoutBeratBadan
                    val Aktivitas = itemBinding?.layoutAktivitas
                    val LamaLatihan = itemBinding?.layoutLamaLatihan
                    val Tanggal = itemBinding?.layoutTanggal

                    if(errors.getString("errors").contains("berat_badan")){
                        BeratBadan?.setError(errors.getJSONObject("errors").getJSONArray("berat_badan").get(0).toString())
                    }
                    if(errors.getString("errors").contains("aktivitas")){
                        Aktivitas?.setError(errors.getJSONObject("errors").getJSONArray("aktivitas").get(0).toString())
                    }
                    if(errors.getString("errors").contains("lama_latihan")){
                        LamaLatihan?.setError(errors.getJSONObject("errors").getJSONArray("lama_latihan").get(0).toString())
                    }
                    if(errors.getString("errors").contains("tanggal")){
                        Tanggal?.setError(errors.getJSONObject("errors").getJSONArray("tanggal").get(0).toString())
                    }

                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@ActivityAddEditHistory, e.message, Toast.LENGTH_SHORT).show()
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
                    val requestBody = gson.toJson(history)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }

        queue!!.add(stringRequest)

//        }

        
    }

    private fun updateHistory(id: Long, token: String){
        setLoading(true)
        println("test" + itemBinding?.etBeratBadan?.text.toString().toFloatOrNull())
        val history = History(
            id,
            etBeratBadan!!.text.toString().toFloat(),
            edAktivitas!!.text.toString(),
            etLamaLatihan!!.text.toString().toInt(),
            etTanggal!!.text.toString(),
            "null"
        )
        val stringRequest: StringRequest =
            object: StringRequest(Method.PUT, HistoryApi.UPDATE_URL + id, Response.Listener { response ->
                val gson = Gson()
                var history = gson.fromJson(response, History::class.java)

                if(history != null)
                    Toast.makeText(this@ActivityAddEditHistory, "Data berhasil diubah", Toast.LENGTH_SHORT).show()

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
                    Toast.makeText(this@ActivityAddEditHistory, e.message, Toast.LENGTH_SHORT).show()
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
                    val requestBody = gson.toJson(history)
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