package com.example.funnytimesvendorapp.Models

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPPropPhoto(
    @SerializedName("id")
    var PhotoId:Int? = null,
    var PhotoType:String = "old",
    @SerializedName("src")
    var PhotoUriString:String = "",
    var PhotoUri:Uri? = null

):Serializable