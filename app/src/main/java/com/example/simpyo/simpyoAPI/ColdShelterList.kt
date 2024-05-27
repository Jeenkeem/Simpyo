package com.example.simpyo.simpyoAPI

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.simpyo.dataclasses.ColdShelterData
import com.example.simpyo.interfaces.ColdShelterInterface

class ColdShelterList {
    fun getColdShelterList(): List<ColdShelterData>? {
        val retrofit = RetrofitClientObject.getInstance()
        val service = retrofit.create(ColdShelterInterface::class.java)
        var coldShelterList : List<ColdShelterData>? = emptyList()

        service.requestColdShelterData().enqueue(object : Callback<List<ColdShelterData>> {
            override fun onResponse(
                call: Call<List<ColdShelterData>>,
                response: Response<List<ColdShelterData>>
            ) {
                coldShelterList = response.body()
            }

            override fun onFailure(call: Call<List<ColdShelterData>>, t: Throwable) {

            }
        })

        return coldShelterList
    }
}