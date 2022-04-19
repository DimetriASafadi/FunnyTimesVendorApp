package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTProAttribute(
    @SerializedName("name")
    var AttrName:String? = "",
    @SerializedName("id")
    val AttrId:Int? = null,
    @SerializedName("attribute_id")
    val AttrTypeId:Int? = null,
    var AttributeIsSelected:Boolean = false
):Serializable
