package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPMyBook(

    @SerializedName("id")
    var BookId:Int? = null,
    @SerializedName("name")
    var BookName:String? = "",
    @SerializedName("total")
    var BookTotal:Int? = null,
    @SerializedName("payment_status")
    var BookPaymentStatus:Int? = null,
    @SerializedName("store_name")
    var BookStoreName:String? = ";",
    @SerializedName("status")
    var BookStatus:String? = "",
    @SerializedName("type")
    var BookType:String? = "",
    @SerializedName("img")
    var BookImg:String? = "",
    @SerializedName("created_at")
    var BookCreatedAt:String? = "",
    @SerializedName("username")
    var BookUsername:String? = "",
    @SerializedName("address")
    var BookAddress:String? = "",
    @SerializedName("status_arabic")
    var BookStatusArabic:String? = ""


):Serializable
