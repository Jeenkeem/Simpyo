package com.example.simpyo.API

import com.google.gson.annotations.SerializedName

data class HeatShelterData (
    @SerializedName("r_seq_no")
    val shelter_num : String?,
    @SerializedName("equp_type")
    val shelter_type : String?,
    @SerializedName("r_area_nm")
    val shelter_name : String?,
    @SerializedName("r_detl_add")
    val shelter_add : String?,
    @SerializedName("use_prnb")
    val available_person : String?,
    @SerializedName("cler1_cnt")
    val fan_own : String?,
    @SerializedName("cler2_cnt")
    val aircon_own : String?,
    @SerializedName("chk1_yn")
    val night_open : String?,
    @SerializedName("chk2_yn")
    val holiday_open : String?,
    @SerializedName("chk3_yn")
    val accom_available : String?,
    @SerializedName("dt_start")
    val start_date : String?,
    @SerializedName("dt_end")
    val end_date : String?,
    @SerializedName("xx")
    val x_coor : Double?,
    @SerializedName("yy")
    val y_coor : Double?
)