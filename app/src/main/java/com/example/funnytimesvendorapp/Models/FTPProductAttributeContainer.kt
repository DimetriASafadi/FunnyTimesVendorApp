package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPProductAttributeContainer(


    @SerializedName("id")
    var ContainerId:Int? = null,
    @SerializedName("name")
    var ContainerName:String? = "",
    @SerializedName("icon")
    var ContainerIcon:String? = "",
    @SerializedName("sub_category_id")
    var ContainerSubCatId:Int? = null,
    @SerializedName("attributesValue")
    var ContainerAttributes:ArrayList<FTPProductAttribute> = ArrayList()


):Serializable