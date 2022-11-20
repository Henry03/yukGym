package com.example.yukgym

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.databinding.ActivityHistoryBinding
import com.example.yukgym.hardware.CustomInfoWindow
import com.example.yukgym.hardware.ModelMain
import com.example.yukgym.volley.api.ProfileApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import java.io.IOException
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.charset.StandardCharsets


class FragmentHome : Fragment() {
    lateinit var assetManager:AssetManager
    var modelMainList: MutableList<ModelMain> = ArrayList()
    lateinit var mapController: MapController
    lateinit var overlayItem: ArrayList<OverlayItem>

    private var queue: RequestQueue? = null

    var sharedPreferences: SharedPreferences? = null


    private fun getLocationMarker() {
        try {
            val stream = context?.getAssets()?.open("sample_maps.json")

            println("test1")
            print("test2")
            val size = stream!!.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            val strContent = String(buffer, StandardCharsets.UTF_8)
            try {
                val jsonObject = JSONObject(strContent)
                val jsonArrayResult = jsonObject.getJSONArray("results")
                for (i in 0 until jsonArrayResult.length()) {
                    val jsonObjectResult = jsonArrayResult.getJSONObject(i)
                    val modelMain = ModelMain()
                    modelMain.strName = jsonObjectResult.getString("name")
                    modelMain.strVicinity = jsonObjectResult.getString("vicinity")

                    //get lat long
                    val jsonObjectGeo = jsonObjectResult.getJSONObject("geometry")
                    val jsonObjectLoc = jsonObjectGeo.getJSONObject("location")
                    modelMain.latLoc = jsonObjectLoc.getDouble("lat")
                    modelMain.longLoc = jsonObjectLoc.getDouble("lng")
                    modelMainList.add(modelMain)
                }
                initMarker(modelMainList)
            }catch (e: JSONException) {
                e.printStackTrace()
            }
        }catch (ignored: IOException) {
            Toast.makeText(
                activity,
                "Oops, ada yang tidak beres. Coba ulangi beberapa saat lagi.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initMarker(modelList: List<ModelMain>) {
        for (i in modelList.indices) {
            overlayItem = ArrayList()
            overlayItem.add(
                OverlayItem(
                    modelList[i].strName, modelList[i].strVicinity, GeoPoint(
                        modelList[i].latLoc, modelList[i].longLoc
                    )
                )
            )
            val info = ModelMain()
            info.strName = modelList[i].strName
            info.strVicinity = modelList[i].strVicinity

            val marker = Marker(mapView)
            marker.icon = resources.getDrawable(R.drawable.ic_baseline_location_on_24)
            marker.position = GeoPoint(modelList[i].latLoc, modelList[i].longLoc)
            marker.relatedObject = info
            marker.infoWindow = CustomInfoWindow(mapView)
            marker.setOnMarkerClickListener { item, arg1 ->
                item.showInfoWindow()
                true
            }

            mapView.overlays.add(marker)
            mapView.invalidate()
        }
    }

    public override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))
        if(mapView != null) {
            mapView.onResume()
        }
    }

    public override fun onPause() {
        super.onPause()
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))
        if(mapView != null) {
            mapView.onPause()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Proses menghubungkan layout fragment_profile.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))

        val geoPoint = GeoPoint(-7.78165, 110.414497)
        mapView.setMultiTouchControls(true)
        mapView.controller.animateTo(geoPoint)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapController = mapView.controller as MapController
        mapController.setCenter(geoPoint)
        mapController.zoomTo(15)

        getLocationMarker()

        queue = Volley.newRequestQueue(requireContext())

        sharedPreferences = this.getActivity()?.getSharedPreferences("login", Context.MODE_PRIVATE)
        val id = sharedPreferences!!.getString("id", "")
        val token = sharedPreferences!!.getString("token", "")
        val history = view.findViewById<CardView>(R.id.historyCard)

        val dateTxt :TextView =  view.findViewById(R.id.tv_date)
        val weightTxt :TextView =  view.findViewById(R.id.tv_weight)

        getHistoryById(id!!.toLong(), token!!, dateTxt, weightTxt)

        history.setOnClickListener(){
            (activity as ActivityHome).setActivity(ActivityHistory())
        }
    }

    private fun getHistoryById(id: Long, token:String, date: TextView, weight : TextView){

        val stringRequest: StringRequest = object :
            StringRequest(
                Method.GET, ProfileApi.LATEST_HISTORY + id,
                { response ->
                    val jsonObject = JSONObject(response)
                    val tanggal = jsonObject.getJSONObject("data").getString("tanggal")
                    val berat = jsonObject.getJSONObject("data").getString("berat_badan")
                    date.setText(tanggal)
                    weight.setText(berat +" Kg")
                },
                Response.ErrorListener{ error ->

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

}