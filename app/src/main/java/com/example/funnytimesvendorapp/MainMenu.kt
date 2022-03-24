package com.example.funnytimesvendorapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.funnytimesvendorapp.AddNewSection.NewChaletScreen
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.MainMenuSection.BooksSection.BooksFrag
import com.example.funnytimesvendorapp.MainMenuSection.HomeSection.HomeFrag
import com.example.funnytimesvendorapp.MainMenuSection.SettingSection.SettingFrag
import com.example.funnytimesvendorapp.MainMenuSection.UserSection.UserFrag
import com.example.funnytimesvendorapp.databinding.FtpScreenMainMenuBinding

class MainMenu : AppCompatActivity() {
    val commonFuncs = CommonFuncs()
    lateinit var binding:FtpScreenMainMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonFuncs.setLocale2(this,"ar")
        if (commonFuncs.IsInSP(this, Constants.KeyAppLanguage)){
            commonFuncs.setLocale2(this,commonFuncs.GetFromSP(this, Constants.KeyAppLanguage)!!)
        }else{
            commonFuncs.setLocale2(this,"ar")
        }
        binding = FtpScreenMainMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val fragment1: Fragment = HomeFrag()
        val fragment2: Fragment = BooksFrag()
        val fragment3: Fragment = UserFrag()
        val fragment4: Fragment = SettingFrag()
        var active = fragment1
        supportFragmentManager.beginTransaction().add(R.id.main_menu_host_frag, fragment4, "4").hide(
            fragment4
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.main_menu_host_frag, fragment3, "3").hide(
            fragment3
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.main_menu_host_frag, fragment2, "2").hide(
            fragment2
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.main_menu_host_frag, fragment1, "1").commit()
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                    return@setOnItemSelectedListener true
                }
                R.id.nav_books -> {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2
                    return@setOnItemSelectedListener true
                }
                R.id.nav_nothing -> {
                    return@setOnItemSelectedListener false
                }
                R.id.nav_user -> {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment3).commit()
                    active = fragment3
                    return@setOnItemSelectedListener true
                }
                R.id.nav_setting -> {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment4).commit()
                    active = fragment4
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
        binding.AddButton.setOnClickListener {
            startActivity(Intent(this,NewChaletScreen::class.java))
        }


    }
    override fun onResume() {
        super.onResume()
        commonFuncs.setLocale2(this,"ar")
    }
}