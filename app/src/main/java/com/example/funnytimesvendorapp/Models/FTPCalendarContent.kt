package com.example.funnytimesvendorapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FTPCalendarContent(

    @SerializedName("id")
    var ContentId:Int? = null,
    @SerializedName("name")
    var ContentName:String? = "",
    @SerializedName("username")
    var ContentUserName:String? = "",
    @SerializedName("booking_type")
    var ContentBookingType:Int? = null,
    @SerializedName("date")
    var ContentStartDate:String? = "",
    @SerializedName("end_date")
    var ContentEndDate:String? = "",
    @SerializedName("start_time")
    var ContentStartTime:String? = "",
    @SerializedName("end_time")
    var ContentEndTime:String? = "",
    @SerializedName("period")
    var ContentPeriod:Int? = null,
    @SerializedName("nights_count")
    var ContentNightCount:String? = ""

):Serializable