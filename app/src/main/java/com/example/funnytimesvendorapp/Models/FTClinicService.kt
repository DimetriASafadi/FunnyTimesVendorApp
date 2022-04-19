package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTClinicService(
    @SerializedName("id")
    val ServiceId:Int? = null,
    @SerializedName("name")
    val ServiceName:String? = "",
    @SerializedName("price")
    val ServicePrice:String? = "0.0",
    var ServiceIsSelected:Boolean = false
):Serializable
