package com.example.simpyo.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.example.simpyo.simpyoAPI.HeatShelterList
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
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

            heatShelterList.forEachIndexed { index, heatShelter ->
                val latLng = heatShelter.shelter_add?.let { getLatLng(it) }

                if (latLng != null && latLng.isValid) {
                    heatShelterMarker[index].position = latLng
                }

                Log.d(TAG, "Marker set : ${heatShelterMarker[index].position}")
            }

            callback(heatShelterMarker)
        }
    }

    fun getLatLng(addr: String): LatLng? {
        var latLng: LatLng? = null

        val result = runCatching {
            geocoder.getFromLocationName(addr, 1)
        }

        result.onSuccess { addresses ->
            if(addresses != null) {
                val address = addresses[0]
                val lat = address.latitude
                val lng = address.longitude

                if (lat.isNaN() || lng.isNaN()) {
                    Log.w(TAG, "Invalid LatLng for address: $addr")
                    latLng = null
                }

                else {
                    latLng = LatLng(lat, lng)
                }
            }
        }

        return latLng
    }
}