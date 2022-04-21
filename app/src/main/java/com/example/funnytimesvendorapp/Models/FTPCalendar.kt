package com.example.funnytimesvendorapp.Models

import java.io.Serializable

data class FTPCalendar(

    var CalLong:Long? = null,
    var CalDay:Int? = null,
    var CalDayName:String? = "",
    var CalMonthName:String? = "",
    var CalIsSelected:Boolean = false,

):Serializable