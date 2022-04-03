package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPNewService(

    @SerializedName("id")
    var ServiceId:Int? = null,
    @SerializedName("name")
    var ServiceName:String? = null,
    @SerializedName("price")
    var ServicePrice:String? = null,


):Serializable
