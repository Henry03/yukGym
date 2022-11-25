package com.example.yukgym.fragment

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
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yukgym.ActivityHistory
import com.example.yukgym.ActivityHome
import com.example.yukgym.R
import com.example.yukgym.hardware.CustomInfoWindow
import com.example.yukgym.hardware.ModelMain
import com.example.yukgym.volley.api.HistoryApi
import com.example.yukgym.volley.api.ProfileApi
import com.example.yukgym.volley.models.History
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAMarker
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAMarkerHover
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAMarkerStates
import com.google.gson.Gson
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
import java.nio.charset.StandardCharsets
import java.sql.Date


class FragmentHome : Fragment() {

    lateinit var assetManager:AssetManager
    var modelMainList: MutableList<ModelMain> = ArrayList()
    lateinit var mapController: MapController
    lateinit var overlayItem: ArrayList<OverlayItem>

    private var queue: RequestQueue? = null

    private var aaChartModel: AAChartModel? = null

    var sharedPreferences: SharedPreferences? = null


    private fun getLocationMarker() {
        try {
            val stream = context?.getAssets()?.open("sample_maps.json")

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
        sevenDayHistory(id!!.toLong(), token!!)



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

    fun splineChart(weight: MutableList<Double>): AAChartModel {

        return AAChartModel()
            .chartType(AAChartType.Spline)
            .title("Disable Spline Chart Marker Hover Effect")
            .categories(arrayOf(
                "一月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "十一月", "十二月"))
            .markerRadius(0) //marker点半径为0个像素
            .yAxisLineWidth(0)
            .yAxisGridLineWidth(0)
            .legendEnabled(false)
            .series(arrayOf(
                AASeriesElement()
                    .name("Weight")
                    .lineWidth(5.0)
                    .color("rgba(220,20,60,1)") //猩红色, alpha 透明度 1
                    .marker(
                        AAMarker()
                        .states(
                            AAMarkerStates()
                            .hover(
                                AAMarkerHover()
                                .enabled(false))))
                    .data(weight.toTypedArray())
            ))
    }

    private fun sevenDayHistory(id: Long, token: String){
        println(id)
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, HistoryApi.GET_ALL_URL + id, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var history : Array<History> = gson.fromJson(jsonObject.getJSONArray("data").toString(), Array<History>::class.java)
                var weight = MutableList<Double>(10){0.0}
                var date = MutableList<String>(10){""}
                for(hist in history){
                    weight.add(hist.berat_badan.toDouble())
                    date.add(hist.tanggal.toString())
                }
                aaChartModel = AAChartModel()
                    .chartType(AAChartType.Spline)
                    .title("Weight")
                    .categories(date.toTypedArray())
                    .markerRadius(0) //marker点半径为0个像素
                    .yAxisLineWidth(0)
                    .yAxisGridLineWidth(0)
                    .legendEnabled(false)
                    .yAxisTitle("Kg")
                    .series(arrayOf(
                        AASeriesElement()
                            .name("Weight")
                            .lineWidth(5.0)
                            .color("rgba(220,20,60,1)") //猩红色, alpha 透明度 1
                            .marker(
                                AAMarker()
                                    .states(
                                        AAMarkerStates()
                                            .hover(
                                                AAMarkerHover()
                                                    .enabled(false))))
                            .data(weight.toTypedArray())
                    ))

                val aaChartView: AAChartView = requireView().findViewById(R.id.AAChartView)
                aaChartView.aa_drawChartWithChartModel(aaChartModel!!)
            }, Response.ErrorListener { error ->

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