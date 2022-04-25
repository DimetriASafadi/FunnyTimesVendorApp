package com.example.funnytimesvendorapp.MainMenuSection.SettingSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnytimesvendorapp.databinding.TScreenContactUsBinding

class ContactUsScreen : AppCompatActivity() {


    lateinit var binding:TScreenContactUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TScreenContactUsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.backBtn.setOnClickListener {
            finish()
        }

    }
}