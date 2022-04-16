package com.example.funnytimesvendorapp.OrBokSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.databinding.FtpScreenProductBinding

class ProductScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



    }
}