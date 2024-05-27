package com.example.simpyo.simpyoAPI

import android.content.ContentValues.TAG
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.simpyo.dataclasses.ColdShelterData
import com.example.simpyo.interfaces.ColdShelterInterface

class ColdShelterList {
    fun getColdShelterList(callback: (List<ColdShelterData>?) -> Unit) {
        val retrofit = RetrofitClientObject.getInstance()
        val service = retrofit.create(ColdShelterInterface::class.java)

        service.requestColdShelterData().enqueue(object : Callback<List<ColdShelterData>> {
            override fun onResponse(
                call: Call<List<ColdShelterData>>,
                response: Response<List<ColdShelterData>>
            ) {
                Log.d(TAG, "통신 성공 : ${response.raw()}")
                callback(response.body())
            }

            override fun onFailure(call: Call<List<ColdShelterData>>, t: Throwable) {
                Log.d(TAG, "통신 실패")
                callback(null)
            }
        })
    }
}