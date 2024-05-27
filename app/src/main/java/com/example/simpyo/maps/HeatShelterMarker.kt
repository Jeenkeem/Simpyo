package com.example.simpyo.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.util.Log
import com.example.simpyo.R
import com.example.simpyo.simpyoAPI.HeatShelterList
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class HeatShelterMarker(private val context: Context) {
    private val geocoder = Geocoder(context, Locale.KOREA)

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
                    val addr = heatShelter.shelter_add

                    if (addr != null) {
                        val latLng = getLatLngAsync(addr)

                        if (latLng != null && latLng.isValid) {
                            heatShelterMarker[index].position = latLng
                            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.simpyo_marker)
                            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 34, 48, false)
                            heatShelterMarker[index].icon = OverlayImage.fromBitmap(resizedBitmap)
                        }

                        else {
                            Log.w(TAG, "Invalid LatLng for address: $addr")
                        }
                    }

                    else {
                        Log.w(TAG, "Address is null for HeatShelter at index $index")
                    }

                    Log.d(TAG, "Marker set : ${heatShelterMarker[index].position}")
                }
            }

            callback(heatShelterMarker)
        }
    }


    private suspend fun getLatLngAsync(addr: String): LatLng? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocationName(addr, 1)

                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val lat = address.latitude
                    val lng = address.longitude

                    if (lat.isNaN() || lng.isNaN()) {
                        Log.w(TAG, "Invalid LatLng for address: $addr")
                        null
                    }

                    else {
                        LatLng(lat, lng)
                    }
                }

                else {
                    Log.w(TAG, "No address found for: $addr")
                    null
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error getting LatLng from address: $addr", e)
                null
            }
        }
    }
}