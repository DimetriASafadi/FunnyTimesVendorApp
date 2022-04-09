package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPOrdBok(
    @SerializedName("id")
    var ObId:Int? = null,
    @SerializedName("name")
    var ObName:String? = null,
    @SerializedName("total")
    var ObTotal:String? = null,
    @SerializedName("payment_status")
    var ObPaymentStatus:String? = null,
    @SerializedName("store_name")
    var ObStoreName:String? = null,
    @SerializedName("status")
    var ObStatus:String? = null,
    @SerializedName("type")
    var ObType:String? = null,
    @SerializedName("img")
    var ObImg:String? = null,
    @SerializedName("created_at")
    var ObCreatedAt:String? = null,

):Serializable
