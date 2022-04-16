package com.example.funnytimesvendorapp.OrBokSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.databinding.FtpScreenServiceBinding

class ServiceScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenServiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }
}