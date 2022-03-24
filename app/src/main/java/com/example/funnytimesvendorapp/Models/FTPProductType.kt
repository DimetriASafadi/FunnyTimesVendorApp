package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPProductType(

    @SerializedName("id")
    var TypeId:Int? = null,
    @SerializedName("name")
    var TypeName:String? = "",
    @SerializedName("icon")
    var TypeIcon:String? = "",
    @SerializedName("attribute")
    var TypeAttrContianers:ArrayList<FTPProductAttributeContainer> = ArrayList()

):Serializable