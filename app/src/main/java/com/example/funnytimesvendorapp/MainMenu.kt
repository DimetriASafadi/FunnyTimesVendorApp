package com.example.funnytimesvendorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs

class MainMenu : AppCompatActivity() {
    val commonFuncs = CommonFuncs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonFuncs.setLocale2(this,"ar")
        setContentView(R.layout.ftp_screen_main_menu)

        


    }

    override fun onResume() {
        super.onResume()
        commonFuncs.setLocale2(this,"ar")

    }
}