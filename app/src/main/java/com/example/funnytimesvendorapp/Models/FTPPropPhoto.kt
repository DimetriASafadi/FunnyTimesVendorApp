package com.example.funnytimesvendorapp.Models

import android.net.Uri
import java.io.Serializable

data class FTPPropPhoto(
    var PhotoId:Int? = null,
    var PhotoType:String = "new",
    var PhotoUriString:String = "",
    var PhotoUri:Uri? = null

):Serializable