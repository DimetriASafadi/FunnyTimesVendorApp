package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPItemAttributes(

    @SerializedName("name")
    var AttributeName:String? = "",
    @SerializedName("value")
    var AttributeValue:String? = "",

):Serializable
