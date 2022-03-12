package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPSubCategory(
    @SerializedName("id")
    var SubCatId:Int? = null,
    @SerializedName("name")
    var SubCatName:String? = "",
    @SerializedName("icon")
    var SubCatIcon:String? = "",

):Serializable
