package com.example.simpyo.interfaces

import com.example.simpyo.dataclasses.ColdShelterData
import retrofit2.Call
import retrofit2.http.GET

interface ColdShelterInterface {
    @GET("getColdShelter.php")
    fun requestColdShelterData(): Call<List<ColdShelterData>>
    /*
        @FormUrlEncoded
        @POST("getHeatShelter.php")
        fun getHeatShelterData(
            // @Field("shelter_num") shelter_num:String,
            // @Field("shelter_type") shelter_type:String,
            @Field("shelter_name") shelter_name:String
            // @Field("shelter_add") shelter_add:String,
            // @Field("available_person") available_person:String,
            // @Field("fan_reserves") fan_reserves:String,
            // @Field("aircon_reserves") aircon_reserves:String,
            // @Field("night_open") night_open:String,
            // @Field("holiday_open") holiday_open:String,
            // @Field("accom_available") accom_available:String,
            // @Field("start_date") start_date:String,
            // @Field("end_date") end_date:String,
            // @Field("x_coor") x_coor:Double,
            // @Field("y_coor") y_coor:Double
        ): Call<HeatShelterData>
     */
}