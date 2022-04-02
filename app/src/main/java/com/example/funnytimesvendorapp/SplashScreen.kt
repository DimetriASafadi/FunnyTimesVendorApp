package com.example.funnytimesvendorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyAppLanguage
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyOpenBefore
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyUserToken
import com.example.funnytimesvendorapp.SectionAuth.SignInScreen
import com.example.funnytimesvendorapp.SectionOnBoard.OnBoardScreen
import java.util.*

class SplashScreen : AppCompatActivity() {

    val commonFuncs = CommonFuncs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonFuncs.setLocale2(this,"ar")
        if (commonFuncs.IsInSP(this,KeyAppLanguage)){
            commonFuncs.setLocale2(this,commonFuncs.GetFromSP(this,KeyAppLanguage)!!)
        }else{
            commonFuncs.setLocale2(this,"ar")
        }
        setContentView(R.layout.ftp_screen_splash)
        if (commonFuncs.GetFromSP(this,KeyOpenBefore) == "Yes"){
            if (commonFuncs.GetFromSP(this,KeyUserToken) != "NoValue"){
                Log.e("Token",commonFuncs.GetFromSP(this,KeyUserToken).toString())
                finishSplashToMainMenu()
            }else{
                finishSplashToSignIn()
            }
        }else{
            finishSplashToOnBoard()
        }

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
    fun finishSplashToMainMenu(){
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@SplashScreen, MainMenu::class.java).apply {}
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
    fun finishSplashToSignIn(){
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@SplashScreen, SignInScreen::class.java).apply {}
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}