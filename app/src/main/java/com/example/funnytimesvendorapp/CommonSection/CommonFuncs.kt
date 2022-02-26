package com.example.funnytimesvendorapp.CommonSection

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.example.funnytimesvendorapp.CommonSection.Constants.AppSPName
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyAppLanguage
import java.util.*

class CommonFuncs {

    val SPName:String = AppSPName


    @Suppress("DEPRECATION")
    fun setLocale2(context: Activity, langua:String) {
        WriteOnSP(context,KeyAppLanguage,langua)
        //Log.e("Lan",session.getLanguage());
        val locale = Locale(langua)
        val config = Configuration(context.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.baseContext.resources.updateConfiguration(config,
            context.baseContext.resources.displayMetrics)
    }


    fun GetFromSP(context: Context, key: String):String?{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "NoValue")
    }
    fun GetLongFromSP(context: Context, key: String):Long?{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        return sharedPreferences.getLong(key, 0)
    }
    fun WriteOnSP(context: Context, key: String, value: String){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
        editor.commit()
    }
    fun WriteLongOnSP(context: Context, key: String, value: Long){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
        editor.commit()
    }
    fun DeleteFromSP(context: Context, key: String){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
        editor.commit()
    }
    fun IsInSP(context: Context, key: String):Boolean{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SPName,
            Context.MODE_PRIVATE)
        return sharedPreferences.contains(key)
    }
}