package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPPropertyAttributeContainer(
    @SerializedName("id")
    var ContainerId:Int? = null,
    @SerializedName("name")
    var ContainerName:String? = "",
    @SerializedName("description")
    var ContainerDesc:String? = "",
    @SerializedName("sub_category_id")
    var ContainerSubCatId:Int? = null,
    @SerializedName("attributesValue")
    var ContainerAttributes:ArrayList<FTPPropertyAttribute>? = ArrayList()

):Serializable
