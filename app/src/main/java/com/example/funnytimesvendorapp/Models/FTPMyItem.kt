package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPMyItem(

    @SerializedName("id")
    var ItemId:Int? = null,
    @SerializedName("name")
    var ItemName:String? = "",
    @SerializedName("rate")
    var ItemRate:Int? = null,
    @SerializedName("img")
    var ItemImg:String? = "",
    @SerializedName("reviews")
    var ItemReviews:Int?= null,
    @SerializedName("created_at")
    var ItemCreateAt:String? = ""

):Serializable