package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPOrBokBar(

    @SerializedName("sums")
    var BarSum:String? = "",
    @SerializedName("months")
    var BarMonths:String? = ""


):Serializable
