package com.example.funnytimesvendorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.SectionOnBoard.OnBoardScreen
import java.util.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ftp_screen_splash)

        finishSplashToOnBoard()

    }

    fun finishSplashToOnBoard(){
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@SplashScreen, OnBoardScreen::class.java).apply {}
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}