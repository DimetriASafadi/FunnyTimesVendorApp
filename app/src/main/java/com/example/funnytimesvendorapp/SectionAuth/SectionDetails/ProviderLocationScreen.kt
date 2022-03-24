package com.example.funnytimesvendorapp.SectionAuth.SectionDetails

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.CommonSection.Constants.LOCATION_REQUEST_CODE
import com.example.funnytimesvendorapp.CommonSection.Constants.REQUEST_CODE_CHECK_SETTINGS
import com.example.funnytimesvendorapp.Models.FTPCategory
import com.example.funnytimesvendorapp.databinding.FtpScreenProviderLocationBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class ProviderLocationScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenProviderLocationBinding
    lateinit var fusedLocationClient:FusedLocationProviderClient
    val commonFuncs = CommonFuncs()

    var phonenum = ""
    var temptoken = ""
    lateinit var ftpCategory :FTPCategory
    var lat = ""
    var lng = ""

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonFuncs.setLocale2(this,"ar")
        if (commonFuncs.IsInSP(this, Constants.KeyAppLanguage)){
            commonFuncs.setLocale2(this,commonFuncs.GetFromSP(this, Constants.KeyAppLanguage)!!)
        }else{
            commonFuncs.setLocale2(this,"ar")
        }
        binding = FtpScreenProviderLocationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.backBtn.setOnClickListener {
            finish()
        }
        phonenum = intent.getStringExtra("phonenum").toString()
        temptoken = intent.getStringExtra("temptoken").toString()
        ftpCategory = intent.getSerializableExtra("ftpCategory") as FTPCategory


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.EnableMyLocation.setOnClickListener {
            if (EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                enableLocationSettings()
            }else{
                EasyPermissions.requestPermissions(
                    this,
                    "لإستخدام هذه الميزة يجب عليك الموافقة على أذونات الموقع وتفعيل خيار الموقع",
                    LOCATION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        binding.EnableLater.setOnClickListener {
            ToDetailsScreen()
        }


    }

    @SuppressLint("MissingPermission")
    private fun enableLocationSettings() {
        val locationRequest: LocationRequest = LocationRequest.create()
            .setInterval(Constants.LOCATION_UPDATE_INTERVAL)
            .setFastestInterval(Constants.LOCATION_UPDATE_FASTEST_INTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        LocationServices
            .getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener(this) { response: LocationSettingsResponse? ->
                Log.e("addOnSuccessListener",response.toString())
                GetLocationTimer()
            }
            .addOnFailureListener(this) { ex: Exception? ->
                if (ex is ResolvableApiException) {
                    // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                    try {
                        Log.e("ex",ex.message.toString())
                        ex.startResolutionForResult(this, Constants.REQUEST_CODE_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.e("sendEx",sendEx.toString())
                    }
                }
            }
    }


    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE_CHECK_SETTINGS === requestCode) {
            if (RESULT_OK == resultCode) {
                Log.e("REQUEST_CODE_CHECK_SETTINGS","Granted")
                GetLocationTimer()
                //user clicked OK, you can startUpdatingLocation(...);
            } else {
                commonFuncs.showDefaultDialog(this,"فشل العملية","فشل عملية الحصول على الموقع , لقد رفضت تشغيل خيار الموقع .")
                Log.e("REQUEST_CODE_CHECK_SETTINGS","Denied")
                //user clicked cancel: informUserImportanceOfLocationAndPresentRequestAgain();
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode:Int,
                                            permissions:Array<String>,
                                            grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // EasyPermissions handles the request result.
        if (EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            Log.e("hasPermissions","Granted")
            enableLocationSettings()
        }else{
            Log.e("hasPermissions","Denied")
            commonFuncs.showDefaultDialog(this,"فشل العملية","فشل عملية الحصول على الموقع , لقد رفضت إعطاء إذن الموقع .")
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun GetLocationTimer(){
        commonFuncs.showLoadingDialog(this)
        Timer().schedule(object : TimerTask() {
            @SuppressLint("MissingPermission")
            override fun run() {
                fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                    commonFuncs.hideLoadingDialog()
                    if (location != null){
                        lat = location.latitude.toString()
                        lng = location.longitude.toString()
                        Log.e("locationlat",location.latitude.toString())
                        Log.e("locationlng",location.longitude.toString())
                        ToDetailsScreen()
                    }else{
                        GetLocationTimer()
                    }
                }

            }
        }, 2000)
    }


    fun ToDetailsScreen(){
        val intent = Intent(this@ProviderLocationScreen, ProviderDetailsScreen::class.java).apply {}
        intent.putExtra("phonenum",phonenum)
        intent.putExtra("temptoken",temptoken)
        intent.putExtra("ftpCategory",ftpCategory)
        intent.putExtra("lat",lat)
        intent.putExtra("lng",lng)
        startActivity(intent)
    }

}