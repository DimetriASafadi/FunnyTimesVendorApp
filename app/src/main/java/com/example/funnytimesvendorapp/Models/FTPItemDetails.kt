package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPItemDetails(

    @SerializedName("id")
    var ItemId:Int? = null,
    @SerializedName("name")
    var ItemName:String? = "",
    @SerializedName("star")
    var ItemStar:Int? = null,
    @SerializedName("type")
    var ItemType:String? = "",
    @SerializedName("venodr_name")
    var ItemVendorName:String? = "",
    @SerializedName("address")
    var ItemAddress:String? = "",
    @SerializedName("price")
    var ItemPrice:String? = "",
    @SerializedName("img")
    var ItemImg:String? = "",
    @SerializedName("is_favourite")
    var ItemIsFavorite:String? = "",
    @SerializedName("count_rate")
    var ItemReviews:Int? = null,
    @SerializedName("created_at")
    var ItemCreatedAt:String? = null,


):Serializable