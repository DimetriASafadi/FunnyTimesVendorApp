package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPMyOrderItem(

    @SerializedName("data")
    var ItemDetails:FTPItemDetails? = null,
    @SerializedName("qty")
    var ItemQuantity:Int? = null,
    @SerializedName("total")
    var ItemTotal:String? = "",
    @SerializedName("price")
    var ItemPrice:String? = "",
    @SerializedName("attributes")
    var ItemAttributes:ArrayList<FTPItemAttributes> = ArrayList()




    ):Serializable