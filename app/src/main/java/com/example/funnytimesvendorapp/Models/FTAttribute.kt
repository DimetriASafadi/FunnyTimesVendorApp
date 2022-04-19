package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTAttribute(
    @SerializedName("id")
    val AttributeId:Int? = null,
    @SerializedName("name")
    val AttributeName:String? = "",
    @SerializedName("icon")
    val AttributeIcon:String? = "",
    @SerializedName("value")
    val AttributeValue:String? = ""
):Serializable
