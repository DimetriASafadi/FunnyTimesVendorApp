package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTReview(
    @SerializedName("id")
    val ReviewId:Int? = null,
    @SerializedName("username")
    val ReviewUserName:String? = "",
    @SerializedName("comment")
    val ReviewComment:String? = "",
    @SerializedName("created_at")
    val ReviewDate:String? = "",
    @SerializedName("img")
    val ReviewUserImg:String? = "",
    @SerializedName("rate")
    val ReviewRate:Int? = null

):Serializable