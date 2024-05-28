package com.example.simpyo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.simpyo.maps.ColdShelterMarker
import com.example.simpyo.maps.HeatShelterMarker
import com.example.simpyo.maps.ShelterKey
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlin.math.pow

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap

    private val heatShelterMarkers = mutableListOf<Marker>()
    private val coldShelterMarkers = mutableListOf<Marker>()

    private val builder: Clusterer.Builder<ShelterKey> = Clusterer.Builder<ShelterKey>()
    private val heatShelterClusterer: Clusterer<ShelterKey>
    private val coldShelterClusterer: Clusterer<ShelterKey>

    private lateinit var locationSource: FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    init {
        builder.clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                super.updateClusterMarker(info, marker)

                val bitmap = BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.simpyo_clusterer)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
                marker.icon = OverlayImage.fromBitmap(resizedBitmap)
                marker.captionTextSize = 25f
                marker.captionColor = Color.rgb(100, 149, 237)
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

        heatShelterClusterer = builder.build()
        coldShelterClusterer = builder.build()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)

        val heatShelterButton: ImageButton = findViewById(R.id.heatShelterButton)
        heatShelterButton.setOnClickListener {
            heatShelterClusterer.map = naverMap
            coldShelterClusterer.map = null

            Toast.makeText(this, "무더위 쉼터", Toast.LENGTH_SHORT).show()
        }

        val coldShelterButton: ImageButton = findViewById(R.id.coldShelterButton)
        coldShelterButton.setOnClickListener {
            heatShelterClusterer.map = null
            coldShelterClusterer.map = naverMap

            Toast.makeText(this, "한파 쉼터", Toast.LENGTH_SHORT).show()
        }

        val currentLocationButton: ImageButton = findViewById(R.id.currentLocationButton)
        currentLocationButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        naverMap.locationSource = locationSource

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }

        heatShelterClusterer.map = null
        coldShelterClusterer.map = null

        HeatShelterMarker(this).getHeatShelterMarker { heatShelterMarker ->
            heatShelterMarker?.forEachIndexed { index, marker ->
                if (marker.position.isValid) {
                    marker.map = null // 모든 마커 숨기기
                    heatShelterMarkers.add(marker)
                    heatShelterClusterer.add(ShelterKey(index, marker.position), null)
                }
            }
            //updateMarkers()
        }

        ColdShelterMarker(this).getColdShelterMarker { coldShelterMarker ->
            coldShelterMarker?.forEachIndexed { index, marker ->
                if (marker.position.isValid) {
                    marker.map = null // 모든 마커 숨기기
                    coldShelterMarkers.add(marker)
                    coldShelterClusterer.add(ShelterKey(index, marker.position), null)
                }
            }
            //updateMarkers()
        }

        naverMap.addOnCameraIdleListener {
            //updateMarkers()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
            }
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