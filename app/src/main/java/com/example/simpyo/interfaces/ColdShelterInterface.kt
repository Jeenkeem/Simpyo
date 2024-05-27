package com.example.simpyo.interfaces

import com.example.simpyo.dataclasses.ColdShelterData
import retrofit2.Call
import retrofit2.http.GET

interface ColdShelterInterface {
    @GET("getColdShelter.php")
    fun requestColdShelterData(): Call<List<ColdShelterData>>
}