package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTItemPhoto(
    @SerializedName("id")
    val PhotoId:Int? = null,
    @SerializedName("src")
    val PhoneUrl:String? = ""
):Serializable