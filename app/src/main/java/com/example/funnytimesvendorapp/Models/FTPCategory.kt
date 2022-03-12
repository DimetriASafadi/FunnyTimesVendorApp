package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class FTPCategory(
    @SerializedName("name")
    var CategoryName:String? = "",
    @SerializedName("id")
    var CategoryId:Int? = null,
    @SerializedName("img")
    var CategoryImg:String? = "",
    @SerializedName("sub_categorises")
    var CategorySubs:List<FTPSubCategory>?
    ):Serializable
