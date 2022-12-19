package com.example.yukgym.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.ActivityAddEditSchedule
import com.example.yukgym.R
import com.example.yukgym.databinding.FragmentScheduleBinding
import com.example.yukgym.databinding.LayoutLoadingBinding
import com.example.yukgym.volley.adapters.ScheduleAdapter
import com.example.yukgym.volley.api.ScheduleApi
import com.example.yukgym.volley.models.Schedule
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class FragmentScheduleVolley  : Fragment() {
    private var srSchedule: SwipeRefreshLayout? = null
    private var adapter: ScheduleAdapter? = null
    private var svSchedule: SearchView? = null
    private var layoutLoading: LayoutLoadingBinding? = null
    private var queue: RequestQueue? = null

    var _binding: FragmentScheduleBinding? = null
    val binding get()= _binding!!
    var sharedPreferences: SharedPreferences? = null


    var id = ""
    var token = ""

    companion object{
        const val LAUNCH_ADD_ACTIVITY = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = this.activity?.getSharedPreferences("login", Context.MODE_PRIVATE)
        id = sharedPreferences?.getString("id", "").toString()
        token = sharedPreferences?.getString("token", "").toString()

        queue = Volley.newRequestQueue(this.activity)
        layoutLoading = binding.layoutLoading
        srSchedule = binding.srSchedule

        svSchedule = binding.svSchedule

        srSchedule?.setOnRefreshListener (SwipeRefreshLayout.OnRefreshListener { allSchedule(token!!) })
        svSchedule?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter!!.filter.filter(p0)
                return false
            }
        })

        val fabAdd = binding.btnCreate
        fabAdd!!.setOnClickListener {
            val intent = Intent(this.activity, ActivityAddEditSchedule::class.java)
//            val i = Intent(this@FragmentScheduleVolley,  ActivityAddEditSchedule::class.java)
            startActivityForResult(intent, LAUNCH_ADD_ACTIVITY)
        }


        adapter = ScheduleAdapter(ArrayList(), object: ScheduleAdapter.OnAdapterListener{
            override fun deleteSchedule(schedule: Schedule) {
                schedule.id?.let { deleteSchedule(it, token!!) }
            }

            override fun editSchedule(schedule: Schedule) {
                val intent = Intent(context, ActivityAddEditSchedule::class.java)
                intent.putExtra("id", schedule.id)
                startActivity(intent)
            }

            })
        val rvProduk = binding.rvSchedule
        rvProduk!!.layoutManager = LinearLayoutManager(this.requireActivity()!!)
        rvProduk!!.adapter = adapter
        allSchedule(token!!)
    }

    override fun onStart() {
        super.onStart()

        sharedPreferences = this.activity?.getSharedPreferences("login", Context.MODE_PRIVATE)
        token = sharedPreferences?.getString("token", "").toString()

        allSchedule(token!!)
    }

    private fun allSchedule(token: String){
        srSchedule!!.isRefreshing = true
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, ScheduleApi.GET_BY_USER_URL, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var schedule : Array<Schedule> = gson.fromJson(jsonObject.getJSONArray("data").toString(), Array<Schedule>::class.java)
                println(schedule)
                adapter!!.setScheduleList(schedule)
                adapter!!.filter.filter(svSchedule!!.query)
                srSchedule!!.isRefreshing = false

//                if(!schedule.isEmpty())
//                    Toast.makeText(context, "Data berhasil diambil", Toast.LENGTH_SHORT).show()
//                else
//                    Toast.makeText(context, "Data Kosong!", Toast.LENGTH_SHORT).show()

            }, Response.ErrorListener { error ->
                srSchedule!!.isRefreshing = false
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(context, errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
//                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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

    fun deleteSchedule(id: Long, token:String){

        setLoading(true)
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, ScheduleApi.DELETE_URL+id,Response.Listener { response ->
                setLoading(false)

                val gson = Gson()
                var schedule = gson.fromJson(response, Schedule::class.java)
                if(schedule != null)
                    Toast.makeText(context, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()

                allSchedule(token)
            }, Response.ErrorListener { error ->
                setLoading(false)
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(context, errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception){
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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
        sharedPreferences = this.activity?.getSharedPreferences("login", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "")
        val id = sharedPreferences?.getString("id", "")
        if(requestCode == LAUNCH_ADD_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                allSchedule(token!!)
            }
        }
    }

    private fun setLoading(isLoading: Boolean){
        if(isLoading){
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            layoutLoading!!.root.visibility = View.INVISIBLE
        }else{
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            layoutLoading!!.root.visibility = View.INVISIBLE
        }
    }
}