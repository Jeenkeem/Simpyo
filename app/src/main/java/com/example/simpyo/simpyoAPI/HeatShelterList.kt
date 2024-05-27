package com.example.simpyo.simpyoAPI

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.simpyo.dataclasses.HeatShelterData
import com.example.simpyo.interfaces.HeatShelterInterface

class HeatShelterList {
    fun getHeatShelterList(): List<HeatShelterData>? {
        val retrofit = RetrofitClientObject.getInstance()
        val service = retrofit.create(HeatShelterInterface::class.java)
        var heatShelterList : List<HeatShelterData>? = emptyList()

        service.requestHeatShelterData().enqueue(object : Callback<List<HeatShelterData>> {
            override fun onResponse(
                call: Call<List<HeatShelterData>>,
                response: Response<List<HeatShelterData>>
            ) {
                heatShelterList = response.body()
            }

            override fun onFailure(call: Call<List<HeatShelterData>>, t: Throwable) {

            }
        })

        return heatShelterList
    }
}