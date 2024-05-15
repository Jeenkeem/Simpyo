package com.example.simpyo.api

import android.content.ContentValues
import android.util.Log
import com.example.simpyo.dataclasses.HeatShelterData
import com.example.simpyo.interfaces.HeatShelterInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HeatShelterList {

        val retrofit = RetrofitClientObject.getInstance()
        val service = retrofit.create(HeatShelterInterface::class.java)

        service.requestHeatShelterData().enqueue(object : Callback<List<HeatShelterData>> {
            override fun onResponse(call: Call<List<HeatShelterData>>, response: Response<List<HeatShelterData>>) {
                Log.d(ContentValues.TAG,"성공")
            }

            override fun onFailure(call: Call<List<HeatShelterData>>, t: Throwable) {
                Log.d(ContentValues.TAG,"안되는거니..?")
                Log.e("stackstrace", "${t.stackTrace}, ${t.printStackTrace()}")
            }
        })
    }

}