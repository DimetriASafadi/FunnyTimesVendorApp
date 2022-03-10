package com.example.funnytimesvendorapp.SectionOnBoard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyOpenBefore
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.SectionAuth.SignInScreen
import com.example.funnytimesvendorapp.databinding.FtpScreenOnboardBinding

class OnBoardScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenOnboardBinding
    val commonFuncs = CommonFuncs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenOnboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.StartNow.setOnClickListener {
            commonFuncs.WriteOnSP(this, KeyOpenBefore,"Yes")
            val intent = Intent(this, SignInScreen::class.java)
            startActivity(intent)
            finish()
        }




    }
}