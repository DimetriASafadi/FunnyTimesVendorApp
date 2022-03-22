package com.example.funnytimesvendorapp.AddNewSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.databinding.FtpScreenNewProductBinding

class NewProductScreen : AppCompatActivity() {
    lateinit var binding:FtpScreenNewProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenNewProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)








    }
}