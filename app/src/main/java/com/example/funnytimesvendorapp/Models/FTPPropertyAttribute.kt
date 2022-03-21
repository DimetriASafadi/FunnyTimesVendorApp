package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPPropertyAttribute(
    @SerializedName("id")
    var AttributeId:Int? = null,
    @SerializedName("name")
    var AttributeName:String? = "",
    @SerializedName("type")
    var AttributeType:String? = "",
    var AttributeValue:String? = "0",
    var IsSelected:Boolean = false
):Serializable
