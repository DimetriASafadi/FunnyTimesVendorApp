package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPTransaction(


    @SerializedName("id")
    var TransId:Int? = null,
    @SerializedName("vendor_id")
    var TransVendorId:Int? = null,
    @SerializedName("vendor_name")
    var TransVendorName:String? = "",
    @SerializedName("name")
    var TransName:String? = "",
    @SerializedName("status")
    var TransStatus:String? = "",
    @SerializedName("amount")
    var TransAmount:String? = "",
    @SerializedName("type")
    var TransType:String? = "",
    @SerializedName("created_at")
    var TransCreatedAt:String? = "",

):Serializable
