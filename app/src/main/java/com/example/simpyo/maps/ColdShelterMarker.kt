package com.example.simpyo.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.simpyo.R
import com.example.simpyo.simpyoAPI.ColdShelterList
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.runBlocking

class ColdShelterMarker(private val context: Context) {
    fun getColdShelterMarker(callback: (Array<Marker>?) -> Unit) {
        ColdShelterList().getColdShelterList { coldShelterList ->
            if (coldShelterList == null) {
                Log.d(TAG, "HeatShelterList is null")
                callback(null)

                return@getColdShelterList
            }

            val coldShelterMarker = Array(coldShelterList.size) { Marker() }

            runBlocking {
                for ((index, coldShelter) in coldShelterList.withIndex()) {
                    val latLng = LatLng(coldShelter.x_coor!!, coldShelter.y_coor!!)

                    coldShelterMarker[index].position = latLng
                    /*
                    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.simpyo_marker)
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 170, 240, false)
                    coldShelterMarker[index].icon = OverlayImage.fromBitmap(resizedBitmap)
                    */

                    Log.d(TAG, "Marker set : ${coldShelterMarker[index].position}")
                }
            }

            callback(coldShelterMarker)
        }
    }
}