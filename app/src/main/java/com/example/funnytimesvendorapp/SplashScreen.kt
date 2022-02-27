package com.example.funnytimesvendorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.SectionOnBoard.OnBoardScreen
import java.util.*

class SplashScreen : AppCompatActivity() {

    val commonFuncs = CommonFuncs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ftp_screen_splash)
        commonFuncs.setLocale2(this,"ar")
        finishSplashToOnBoard()

    }

    fun finishSplashToOnBoard(){
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@SplashScreen, MainMenu::class.java).apply {}
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}