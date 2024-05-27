package com.example.simpyo

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.simpyo.maps.HeatShelterMarker
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var naverMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        HeatShelterMarker(this).getHeatShelterMarker { heatShelterMarker ->
            if(heatShelterMarker != null) {
                for(marker in heatShelterMarker) {
                    if(marker.position.isValid)
                        marker.map = this.naverMap
                }
            }

            else {
                Log.d(TAG, "Log.d(ContentValues.TAG, \"HeatShelterMarker array is null\")")
            }
        }
    }


}