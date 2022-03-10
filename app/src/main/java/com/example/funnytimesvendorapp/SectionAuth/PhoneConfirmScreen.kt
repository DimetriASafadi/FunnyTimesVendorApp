package com.example.funnytimesvendorapp.SectionAuth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.databinding.FtpScreenPhoneConfirmBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class PhoneConfirmScreen : AppCompatActivity() {


    lateinit var binding:FtpScreenPhoneConfirmBinding
    val commonFuncs = CommonFuncs()
    var temptoken = ""
    var comingFrom = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenPhoneConfirmBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        temptoken = intent.getStringExtra("TempToken").toString()
        comingFrom = intent.getStringExtra("comingFrom").toString()



        binding.CountryPicker.registerCarrierNumberEditText(binding.PhoneNumber)

        binding.ClearPhoneNum.setOnClickListener {
            binding.PhoneNumber.setText("")
        }

        binding.SendCodeToPhone.setOnClickListener {
            Log.e("Phone",binding.CountryPicker.fullNumberWithPlus.toString())

            if (binding.PhoneNumber.text.toString().isNullOrEmpty()){
                binding.PhoneNumber.error = "لا يمكن ترك الحقل فارغ"
                binding.PhoneNumber.requestFocus()
                return@setOnClickListener
            }
            update_phone_Request(binding.CountryPicker.fullNumberWithPlus.toString())
        }
    }

    fun update_phone_Request(phonenum:String) {
        Log.e("PhoneNumber",phonenum)
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "/api/vendor/auth/user/updatePhone"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.POST, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
//                    val data = JSONObject(response.toString()).getJSONObject("data")
//                    val tempToken = data.getString("access_token")
                    val intent = Intent(this,CodeConfirmScreen::class.java)
                    intent.putExtra("comingFrom",comingFrom)
                    intent.putExtra("TempToken",temptoken)
                    startActivity(intent)
                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    Log.e("Error", error.toString())
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"فشل التحقق",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"فشل التحقق","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String,String>()
                    params["phone"] = phonenum
                    return params
                }
                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String,String>()
                    header["Authorization"] = "Bearer $temptoken"
                    return header
                }
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }
}