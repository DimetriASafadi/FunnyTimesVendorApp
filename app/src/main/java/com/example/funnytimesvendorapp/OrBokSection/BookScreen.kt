package com.example.funnytimesvendorapp.OrBokSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.databinding.FtpScreenBookBinding

class BookScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenBookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenBookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }
}