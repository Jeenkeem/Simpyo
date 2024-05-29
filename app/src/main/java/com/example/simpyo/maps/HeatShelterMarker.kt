package com.example.simpyo.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.simpyo.simpyoAPI.HeatShelterList
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.runBlocking

class HeatShelterMarker(private val context: Context) {
    fun getHeatShelterMarker(callback: (Array<Marker>?) -> Unit) {
        HeatShelterList().getHeatShelterList { heatShelterList ->
            if (heatShelterList == null) {
                Log.d(TAG, "HeatShelterList is null")
                callback(null)

                return@getHeatShelterList
            }

            val heatShelterMarker = Array(heatShelterList.size) { Marker() }

            runBlocking {
                for ((index, heatShelter) in heatShelterList.withIndex()) {
                    val latLng = LatLng(heatShelter.x_coor!!, heatShelter.y_coor!!)

                    heatShelterMarker[index].position = latLng
                    /*
                    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.simpyo_marker)
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 170, 240, false)
                    heatShelterMarker[index].icon = OverlayImage.fromBitmap(resizedBitmap)
                    */

                    Log.d(TAG, "Marker set : ${heatShelterMarker[index].position}")
                }
            }

            callback(heatShelterMarker)
        }
    }
}