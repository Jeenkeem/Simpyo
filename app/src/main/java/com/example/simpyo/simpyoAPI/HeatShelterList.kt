package com.example.simpyo.simpyoAPI

import android.content.ContentValues.TAG
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.simpyo.dataclasses.HeatShelterData
import com.example.simpyo.interfaces.HeatShelterInterface

class HeatShelterList {
    fun getHeatShelterList(callback: (List<HeatShelterData>?) -> Unit) {
        val retrofit = RetrofitClientObject.getInstance()
        val service = retrofit.create(HeatShelterInterface::class.java)

        service.requestHeatShelterData().enqueue(object : Callback<List<HeatShelterData>> {
            override fun onResponse(
                call: Call<List<HeatShelterData>>,
                response: Response<List<HeatShelterData>>
            ) {
                Log.d(TAG, "통신 성공 : ${response.raw()}")
                callback(response.body())
            }

            override fun onFailure(call: Call<List<HeatShelterData>>, t: Throwable) {
                Log.d(TAG, "통신 실패")
                callback(null)
            }
        })
    }
}