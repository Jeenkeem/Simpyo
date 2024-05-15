package com.example.simpyo

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.simpyo.API.HeatShelterData
import com.example.simpyo.API.HeatShelterInterface
import com.example.simpyo.API.RetrofitClientObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val retrofit = RetrofitClientObject.getInstance()
        val service = retrofit.create(HeatShelterInterface::class.java)

        service.requestHeatShelterData().enqueue(object : Callback<List<HeatShelterData>>{
            override fun onResponse(call: Call<List<HeatShelterData>>, response: Response<List<HeatShelterData>>) {
                Log.d(TAG,"성공")
            }

            override fun onFailure(call: Call<List<HeatShelterData>>, t: Throwable) {
                Log.d(TAG,"안되는거니..?")
                Log.e("stackstrace", "${t.stackTrace}, ${t.printStackTrace()}")
            }
        })
    }
}