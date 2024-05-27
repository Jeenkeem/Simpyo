package com.example.simpyo.interfaces

import com.example.simpyo.dataclasses.HeatShelterData
import retrofit2.Call
import retrofit2.http.GET

interface HeatShelterInterface {
    @GET("getHeatTest.php")
    fun requestHeatShelterData(): Call<List<HeatShelterData>>
}