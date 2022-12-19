package com.example.yukgym

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.R
import com.example.yukgym.databinding.ActivityBloodPressureBinding
import com.example.yukgym.volley.adapters.BloodPressureAdapter
import com.example.yukgym.volley.api.BloodPressureApi
import com.example.yukgym.volley.models.BloodPressure
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ActivityBloodPressure : AppCompatActivity() {
    private var srBloodPressure: SwipeRefreshLayout? = null
    private var adapter: BloodPressureAdapter? = null
    private var svBloodPressure: SearchView? = null
    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    var itemBinding: ActivityBloodPressureBinding? = null
    var sharedPreferences: SharedPreferences? = null

    companion object{
        const val LAUNCH_ADD_ACTIVITY = 123
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemBinding = ActivityBloodPressureBinding.inflate(layoutInflater)
        setContentView(itemBinding?.root)


        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString("id", "")
        val token = sharedPreferences?.getString("token", "")

        queue = Volley.newRequestQueue(this)
        layoutLoading = findViewById(R.id.layout_loading)
        srBloodPressure = itemBinding?.srBloodPressure

        svBloodPressure = itemBinding?.svBloodPressure

        srBloodPressure?.setOnRefreshListener (SwipeRefreshLayout.OnRefreshListener { allBloodPressure(token!!) })
        svBloodPressure?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter!!.filter.filter(p0)
                return false
            }
        })

        val fabAdd = itemBinding?.fabAdd
        fabAdd!!.setOnClickListener {
            val i = Intent(this@ActivityBloodPressure,  ActivityAddEditBloodPressure::class.java)
            startActivityForResult(i, LAUNCH_ADD_ACTIVITY)
        }

        val rvProduk = itemBinding?.rvBloodPressure
        adapter = BloodPressureAdapter(ArrayList(), this)
        rvProduk!!.layoutManager = LinearLayoutManager(this)
        rvProduk!!.adapter = adapter
        allBloodPressure(token!!)
    }

    override fun onStart() {
        super.onStart()
        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)

        val token = sharedPreferences?.getString("token", "")
        allBloodPressure(token!!)
    }

    private fun allBloodPressure(token: String){
        srBloodPressure!!.isRefreshing = true
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, BloodPressureApi.GET_BY_USER_URL, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var bloodPressure : Array<BloodPressure> = gson.fromJson(jsonObject.getJSONArray("data").toString(), Array<BloodPressure>::class.java)
                println(bloodPressure)
                adapter!!.setBloodPressureList(bloodPressure)
                adapter!!.filter.filter(svBloodPressure!!.query)
                srBloodPressure!!.isRefreshing = false

                if(!bloodPressure.isEmpty())
                    Toast.makeText(this@ActivityBloodPressure, "Data berhasil diambil", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this@ActivityBloodPressure, "Data Kosong!", Toast.LENGTH_SHORT).show()

            }, Response.ErrorListener { error ->
                srBloodPressure!!.isRefreshing = false
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(this@ActivityBloodPressure, errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    Toast.makeText(this@ActivityBloodPressure, e.message, Toast.LENGTH_SHORT).show()
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

    fun deleteBloodPressure(id: Long, token:String){

        setLoading(true)
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, BloodPressureApi.DELETE_URL+id,Response.Listener { response ->
                setLoading(false)

                val gson = Gson()
                var bloodPressure = gson.fromJson(response, BloodPressure::class.java)
                if(bloodPressure != null)
                    Toast.makeText(this@ActivityBloodPressure, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()

                allBloodPressure(token)
            }, Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(this@ActivityBloodPressure, errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception){
                    Toast.makeText(this@ActivityBloodPressure, e.message, Toast.LENGTH_SHORT).show()
                }
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        queue!!.add(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "")
        val id = sharedPreferences?.getString("id", "")
        if(requestCode == LAUNCH_ADD_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                allBloodPressure(token!!)
            }
        }
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