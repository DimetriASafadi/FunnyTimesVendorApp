package com.example.funnytimesvendorapp.AddNewSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.databinding.FtpScreenNewFoodBinding

class NewFoodScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenNewFoodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenNewFoodBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }
}