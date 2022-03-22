package com.example.funnytimesvendorapp.AddNewSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.databinding.FtpScreenNewClinicBinding

class NewClinicScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenNewClinicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenNewClinicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



    }
}