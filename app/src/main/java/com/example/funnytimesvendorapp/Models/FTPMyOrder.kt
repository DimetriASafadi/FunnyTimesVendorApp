package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPMyOrder(

    @SerializedName("id")
    var OrderId:Int? = null,
    @SerializedName("items")
    var OrderItems:ArrayList<FTPMyOrderItem> = ArrayList(),
    @SerializedName("total")
    var OrderTotal:String? = "",
    @SerializedName("payment_status")
    var OrderPaymentStatus:String? = "",
    @SerializedName("store_name")
    var OrderStoreName:String? = "",
    @SerializedName("created_at")
    var OrderCreatedAt:String? = "",
    @SerializedName("status")
    var OrderStatus:String? = "",
    @SerializedName("lat")
    var OrderLat:String? = "",
    @SerializedName("lng")
    var OrderLng:String? = "",
    @SerializedName("username")
    var OrderCustomer:String? = "",
    @SerializedName("payment_gateway")
    var OrderGateway:String? = "",

):Serializable