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
import com.example.funnytimesvendorapp.CommonSection.Constants.APIMain
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyUserID
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyUserToken
import com.example.funnytimesvendorapp.MainMenu
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.SectionAuth.SectionDetails.ProviderCategoryScreen
import com.example.funnytimesvendorapp.SectionAuth.SectionPassword.PhonePasswordScreen
import com.example.funnytimesvendorapp.databinding.FtpScreenSignInBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class SignInScreen : AppCompatActivity() {


    lateinit var binding: FtpScreenSignInBinding
    val commonFuncs = CommonFuncs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonFuncs.setLocale2(this,"ar")
        if (commonFuncs.IsInSP(this, Constants.KeyAppLanguage)){
            commonFuncs.setLocale2(this,commonFuncs.GetFromSP(this, Constants.KeyAppLanguage)!!)
        }else{
            commonFuncs.setLocale2(this,"ar")
        }
        binding = FtpScreenSignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.SSignIn.setOnClickListener {
            val username = binding.SUserEmail.text.toString()
            val password = binding.SUserPassword.text.toString()
            if (username.isNullOrEmpty()){
                binding.SUserEmail.error = "لا يمكن ترك الحقل فارغ"
                binding.SUserEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isNullOrEmpty()){
                binding.SUserPassword.error = "لا يمكن ترك الحقل فارغ"
                binding.SUserPassword.requestFocus()
                return@setOnClickListener
            }
            login_Request(username,password)
        }
        binding.SForgotPass.setOnClickListener {
            startActivity(Intent(this,PhonePasswordScreen::class.java))
        }
        binding.SGoToRegister.setOnClickListener {
            startActivity(Intent(this,SignUpScreen::class.java))
            finish()
        }
    }

    fun login_Request(username:String,password:String) {
        commonFuncs.showLoadingDialog(this)
        val url = APIMain + "api/vendor-app/auth/login"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.POST, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val temptoken = data.getString("access_token")
                    val userid = data.getJSONObject("user").getInt("id")
                    val is_owner = data.getJSONObject("user").getInt("is_owner")
                    val phonenum = data.getJSONObject("user").getString("phone")
                    if (is_owner == 1 && !data.getJSONObject("user").isNull("vendor")){
                        commonFuncs.WriteOnSP(this,KeyUserID,userid.toString())
                        commonFuncs.WriteOnSP(this,KeyUserToken,temptoken)
                        commonFuncs.hideLoadingDialog()
                        val intent = Intent(this,MainMenu::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }else{
                        commonFuncs.hideLoadingDialog()
                        val intent = Intent(this,ProviderCategoryScreen::class.java)
                        intent.putExtra("phonenum",phonenum)
                        intent.putExtra("temptoken",temptoken)
                        intent.putExtra("comingFrom","signin")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"فشل تسجيل الدخول",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"فشل تسجيل الدخول","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String,String>()
                    params["email"] = username
                    params["password"] = password
                    return params
                }
//            override fun getHeaders(): MutableMap<String, String> {
//
//                return headers
//            }
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }
}