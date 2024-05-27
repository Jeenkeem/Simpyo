package com.example.simpyo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.simpyo.maps.ColdShelterMarker
import com.example.simpyo.maps.HeatShelterMarker
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import kotlin.math.pow

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private val heatShelterMarkers = mutableListOf<Marker>()
    private val coldShelterMarkers = mutableListOf<Marker>()

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
            heatShelterMarker?.forEach { marker ->
                if (marker.position.isValid) {
                    marker.map = null // 모든 마커 숨기기
                    heatShelterMarkers.add(marker)
                }
            }
            updateMarkers()
        }

        ColdShelterMarker(this).getColdShelterMarker { coldShelterMarker ->
            coldShelterMarker?.forEach { marker ->
                if (marker.position.isValid) {
                    marker.map = null // 모든 마커 숨기기
                    coldShelterMarkers.add(marker)
                }
            }
            updateMarkers()
        }

        naverMap.addOnCameraIdleListener {
            updateMarkers()
        }
    }

    private fun updateMarkers() {
        val cameraPosition = naverMap.cameraPosition
        val mapBounds = getBounds(cameraPosition.target, cameraPosition.zoom)

        heatShelterMarkers.forEach { marker ->
            val position = marker.position
            val shouldBeVisible = mapBounds.contains(position)

            if (shouldBeVisible && marker.map == null) {
                marker.map = naverMap
            }

            else if (!shouldBeVisible && marker.map != null) {
                marker.map = null
            }
        }

        coldShelterMarkers.forEach { marker ->
            val position = marker.position
            val shouldBeVisible = mapBounds.contains(position)

            if (shouldBeVisible && marker.map == null) {
                marker.map = naverMap
            }

            else if (!shouldBeVisible && marker.map != null) {
                marker.map = null
            }
        }
    }

    private fun getBounds(center: LatLng, zoom: Double): LatLngBounds {
        val widthInMeters = 1000 * 2.0.pow(21 - zoom)
        val offset = widthInMeters / 2
        val southWest = LatLng(center.latitude - offset, center.longitude - offset)
        val northEast = LatLng(center.latitude + offset, center.longitude + offset)

        return LatLngBounds(southWest, northEast)
    }
}