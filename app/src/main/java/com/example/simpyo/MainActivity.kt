package com.example.simpyo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.simpyo.dataclasses.HeatShelterData
import com.example.simpyo.maps.ColdShelterMarker
import com.example.simpyo.maps.HeatShelterMarker
import com.example.simpyo.maps.ShelterKey
import com.example.simpyo.simpyoAPI.HeatShelterList
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
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

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap

    private val builder: Clusterer.Builder<ShelterKey> = Clusterer.Builder<ShelterKey>()
    private val heatShelterClusterer: Clusterer<ShelterKey>
    private val coldShelterClusterer: Clusterer<ShelterKey>

    private lateinit var locationSource: FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    private var shelterCategory = 0 // 0: 카테고리 미설정, 1: 무더위 쉼터, 2: 한파 쉼터

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
        val coldShelterButton: ImageButton = findViewById(R.id.coldShelterButton)

        heatShelterButton.setOnClickListener {
            heatShelterClusterer.map = naverMap
            coldShelterClusterer.map = null
            shelterCategory = 1
            updateSearchCategory()

            heatShelterButton.isSelected = true
            coldShelterButton.isSelected = false
            Toast.makeText(this, "무더위 쉼터", Toast.LENGTH_SHORT).show()
        }

        coldShelterButton.setOnClickListener {
            heatShelterClusterer.map = null
            coldShelterClusterer.map = naverMap
            shelterCategory = 2
            updateSearchCategory()

            coldShelterButton.isSelected = true
            heatShelterButton.isSelected = false
            Toast.makeText(this, "한파 쉼터", Toast.LENGTH_SHORT).show()
        }

        val currentLocationButton: ImageButton = findViewById(R.id.currentLocationButton)
        currentLocationButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        updateSearchCategory()
    }

    private fun searchForShelter(query: String, heatShelterList: List<HeatShelterData>?) {
        // 여기에 검색 로직 추가
        Toast.makeText(this, "Searching for: $query", Toast.LENGTH_SHORT).show()

        if (shelterCategory == 1) {
            // for 문으로 검색한 쉼터 명칭과 일치하는 데이터를 찾음
            heatShelterList?.forEach { shelterData ->
                if (shelterData.shelter_name == query) {
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(shelterData.x_coor!!, shelterData.y_coor!!))
                    cameraUpdate.animate(CameraAnimation.Fly, 1500)
                    naverMap.moveCamera(cameraUpdate)

                    //displayShelterInfo(shelterData) // 일치하는 쉼터 정보를 슬라이딩 패널로 표시
                }
            }
        }
    }

    private fun updateSearchCategory() {
        // AutoCompleteTextView와 버튼 설정
        val searchShelter: AutoCompleteTextView = findViewById(R.id.searchShelter)
        val searchButton: ImageButton = findViewById(R.id.searchButton)

        // 무더위 쉼터 검색
        if(shelterCategory == 1) {
            // HeatShelterList를 통해 데이터 가져오기
            val heatShelterList = HeatShelterList()
            heatShelterList.getHeatShelterList { shelterDataList ->
                val heatShelterNames = shelterDataList?.map { it.shelter_name } ?: listOf()
                val adapter =
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        heatShelterNames
                    )
                searchShelter.setAdapter(adapter)

                // TextWatcher를 추가하여 사용자가 입력할 때 자동완성 기능을 작동시키도록 함
                searchShelter.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        adapter.filter.filter(s)
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                // 검색 버튼 클릭 이벤트 설정
                searchButton.setOnClickListener {
                    val query = searchShelter.text.toString()
                    if (query.isNotEmpty()) {
                        searchForShelter(query, shelterDataList)
                    }
                }
            }
        }

        // 한파 쉼터 검색
        else if(shelterCategory == 2) {

        }

        // 카테고리 미설정
        else {
            searchButton.setOnClickListener {
                Toast.makeText(this, "카테고리 미설정", Toast.LENGTH_SHORT).show()
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
                    //heatShelterMarkers.add(marker)
                    heatShelterClusterer.add(ShelterKey(index, marker.position), null)
                }
            }
        }

        ColdShelterMarker(this).getColdShelterMarker { coldShelterMarker ->
            coldShelterMarker?.forEachIndexed { index, marker ->
                if (marker.position.isValid) {
                    marker.map = null // 모든 마커 숨기기
                    //coldShelterMarkers.add(marker)
                    coldShelterClusterer.add(ShelterKey(index, marker.position), null)
                }
            }
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
}