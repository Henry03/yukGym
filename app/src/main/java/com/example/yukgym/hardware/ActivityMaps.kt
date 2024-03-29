package com.example.yukgym.hardware

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.example.yukgym.R
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_main_menu.*
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

class ActivityMaps : AppCompatActivity(){

    var modelMainList: MutableList<ModelMain> = ArrayList()
    lateinit var mapController: MapController
    lateinit var overlayItem: ArrayList<OverlayItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        val geoPoint1 = GeoPoint(-7.777082, 110.416098)
        val geoPoint2 = GeoPoint(-7.783122, 110.407405)
        val geoPoint3 = GeoPoint(-7.775395, 110.415731)

        mapsView.setMultiTouchControls(true)

        mapsView.controller.animateTo(geoPoint1)
        mapsView.controller.animateTo(geoPoint2)
        mapsView.controller.animateTo(geoPoint3)

        mapsView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapsView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapController = mapsView.controller as MapController

        mapController.setCenter(geoPoint1)
        mapController.setCenter(geoPoint2)
        mapController.setCenter(geoPoint3)

        mapController.zoomTo(15)

        getLocationMarker()
    }

    private fun getLocationMarker() {
        try {
            val stream = assets.open("sample_maps.json")
            val size = stream.available()
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
                this@ActivityMaps,
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

            val marker = Marker(mapsView)
            marker.icon = resources.getDrawable(R.drawable.ic_baseline_location_on_24)
            marker.position = GeoPoint(modelList[i].latLoc, modelList[i].longLoc)
            marker.relatedObject = info
            marker.infoWindow = CustomInfoWindow(mapsView)
            marker.setOnMarkerClickListener { item, arg1 ->
                item.showInfoWindow()
                true
            }

            mapsView.overlays.add(marker)
            mapsView.invalidate()
        }
    }

    public override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if(mapView != null) {
            mapView.onResume()
        }
    }

    public override fun onPause() {
        super.onPause()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if(mapView != null) {
            mapView.onPause()
        }
    }

}