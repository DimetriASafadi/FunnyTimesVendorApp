package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPPropertyCity(
    @SerializedName("id")
    var CityId:Int? = null,
    @SerializedName("name")
    var CityName:String? = "",
    @SerializedName("image")
    var CityImage:String? = ""

):Serializable
