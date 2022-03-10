package com.example.funnytimesvendorapp.SectionAuth.SectionPassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants.APIMain
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.databinding.FtpScreenPasswordCodeConfirmBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class PasswordCodeConfirmScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenPasswordCodeConfirmBinding
    val commonFuncs = CommonFuncs()
    var phonenum = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenPasswordCodeConfirmBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        phonenum = intent.getStringExtra("SendToPhone").toString()
        val phonexx = "*********"+phonenum[phonenum.length-2]+phonenum[phonenum.length-1]
        binding.ResetNumberHint.text = "يرجى إدخال رمز إعادة التعيين الذي تم ارساله على الرقم $phonexx"
        binding.ResetCheckCode.setOnClickListener {
            if (binding.ResetCodePinET.text?.length != 4 ){
                commonFuncs.showDefaultDialog(this,"","يجب إدخال 4 أرقام")
                return@setOnClickListener
            }
//            checkCode_Request(phonenum,binding.ResetCodePinET.text.toString())
        }

        binding.ResetCodePinET.addTextChangedListener {
            if (binding.ResetCodePinET.text?.length == 4 ){
                checkCode_Request(phonenum,binding.ResetCodePinET.text.toString())
            }
        }
    }

    fun checkCode_Request(phonenum:String,code:String) {
        Log.e("PhoneNumber",phonenum)
        commonFuncs.showLoadingDialog(this)
        val url = APIMain + "api/auth/checkCode"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.POST, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val data = JSONObject(response.toString()).getJSONObject("status")
                    val status = data.getString("status").toBoolean()
                    if (status){
                        commonFuncs.hideLoadingDialog()
                        val intent = Intent(this,NewPasswordScreen::class.java)
                        intent.putExtra("SendToPhone",phonenum)
                        startActivity(intent)
                        finish()
                    }else{
                        commonFuncs.showDefaultDialog(this,"فشل إعادة التعيين","حصل خطأ ما")
                    }
                }, Response.ErrorListener { error ->
                    Log.e("Error", error.toString())
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"فشل إعادة التعيين",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"فشل إعادة التعيين","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()
                }) {
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String,String>()
                    params["phone"] = phonenum
                    params["code"] = code
                    return params
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