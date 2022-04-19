package com.example.funnytimesvendorapp.Models

import java.io.Serializable

data class FTProAttrContainer(
    val ContainerName:String? = "",
    val ContainerAttributes:ArrayList<FTProAttribute>
):Serializable