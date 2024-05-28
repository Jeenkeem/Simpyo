package com.example.simpyo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.simpyo.maps.ColdShelterMarker
import com.example.simpyo.maps.HeatShelterMarker
import com.example.simpyo.maps.ShelterKey
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.ClusterMarkerUpdater
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.clustering.LeafMarkerUpdater
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import kotlin.math.pow

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private val heatShelterMarkers = mutableListOf<Marker>()
    private val coldShelterMarkers = mutableListOf<Marker>()
    private val builder: Clusterer.Builder<ShelterKey> = Clusterer.Builder<ShelterKey>()
    private val clusterer: Clusterer<ShelterKey>

    init {
        builder.clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                super.updateClusterMarker(info, marker)

                val bitmap = BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.simpyo_clusterer)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
                marker.icon = OverlayImage.fromBitmap(resizedBitmap)
                marker.captionTextSize = 25f
                marker.captionColor = Color.rgb(100, 12, 12)
                marker.captionText = info.size.toString()
                /*
                marker.icon = if(info.size < 3) {
                    MarkerIcons.CLUSTER_LOW_DENSITY
                }

                else {
                    MarkerIcons.CLUSTER_MEDIUM_DENSITY
                }
                */
            }
        }).leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
            override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                super.updateLeafMarker(info, marker)
                val key = info.key as ShelterKey

                val bitmap = BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.simpyo_marker)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 170, 240, false)
                marker.icon = OverlayImage.fromBitmap(resizedBitmap)
            }
        })

        clusterer = builder.build()
    }
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
        clusterer.map = naverMap

        HeatShelterMarker(this).getHeatShelterMarker { heatShelterMarker ->
            heatShelterMarker?.forEachIndexed { index, marker ->
                if (marker.position.isValid) {
                    marker.map = null // 모든 마커 숨기기
                    heatShelterMarkers.add(marker)
                    clusterer.add(ShelterKey(index, marker.position), null)
                }
            }
            //updateMarkers()
        }

        ColdShelterMarker(this).getColdShelterMarker { coldShelterMarker ->
            coldShelterMarker?.forEachIndexed { index, marker ->
                if (marker.position.isValid) {
                    marker.map = null // 모든 마커 숨기기
                    coldShelterMarkers.add(marker)
                    clusterer.add(ShelterKey(index, marker.position), null)
                }
            }
            //updateMarkers()
        }

        naverMap.addOnCameraIdleListener {
            //updateMarkers()
        }
    }

    private fun updateMarkers() {
        val cameraPosition = naverMap.cameraPosition
        val mapBounds = getBounds(cameraPosition.target, cameraPosition.zoom)


        /*
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
        */
    }

    private fun getBounds(center: LatLng, zoom: Double): LatLngBounds {
        val widthInMeters = 1000 * 2.0.pow(21 - zoom)
        val offset = widthInMeters / 2
        val southWest = LatLng(center.latitude - offset, center.longitude - offset)
        val northEast = LatLng(center.latitude + offset, center.longitude + offset)

        return LatLngBounds(southWest, northEast)
    }
}