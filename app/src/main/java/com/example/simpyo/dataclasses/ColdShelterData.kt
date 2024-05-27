package com.example.simpyo.dataclasses

import com.google.gson.annotations.SerializedName

data class ColdShelterData (
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
    @SerializedName("heat1_cnt")
    val heatfan_own : String?,
    @SerializedName("heat2_cnt")
    val heater_own : String?,
    @SerializedName("heat3_cnt")
    val stove_own : String?,
    @SerializedName("heat4_cnt")
    val radiator_own : String?,
    @SerializedName("chk8_yn")
    val holiday_open : String?,
    @SerializedName("chk9_yn")
    val accom_available : String?,
    @SerializedName("c_dt_strt")
    val start_date : String?,
    @SerializedName("c_dt_end")
    val end_date : String?,
    @SerializedName("g2_xmin")
    val x_coor : Double?,
    @SerializedName("g2_ymin")
    val y_coor : Double?
)