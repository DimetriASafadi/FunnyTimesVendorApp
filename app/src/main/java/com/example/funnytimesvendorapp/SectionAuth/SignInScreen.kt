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
import com.example.funnytimesvendorapp.CommonSection.Constants.APIMain
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyUserID
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyUserToken
import com.example.funnytimesvendorapp.MainMenu
import com.example.funnytimesvendorapp.R
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
                binding.SUserEmail.error = "لا يمكن ترك الحقل فارغ"
                binding.SUserEmail.requestFocus()
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
        binding.ContAsGuest.setOnClickListener {
            val intent = Intent(this,MainMenu::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    fun login_Request(username:String,password:String) {
        commonFuncs.showLoadingDialog(this)
        val url = APIMain + "api/vendor/auth/login"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.POST, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val token = data.getString("access_token")
                    val userid = data.getJSONObject("user").getInt("id")
                    val userphone = data.getJSONObject("user").getString("phone").toString()
                    val isactive = data.getJSONObject("user").getString("status")
                    if (isactive == "active"){
                        commonFuncs.WriteOnSP(this,KeyUserID,userid.toString())
                        commonFuncs.WriteOnSP(this,KeyUserToken,token)
                        commonFuncs.hideLoadingDialog()
                        val intent = Intent(this,MainMenu::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }else{
                        if (userphone == "null"){
                            val intent = Intent(this,PhoneConfirmScreen::class.java)
                            intent.putExtra("comingFrom","signin")
                            intent.putExtra("TempToken",token)
                            startActivity(intent)
                            commonFuncs.hideLoadingDialog()
                            finish()
                        }else{
                            val intent = Intent(this,CodeConfirmScreen::class.java)
                            intent.putExtra("comingFrom","signin")
                            intent.putExtra("TempToken",token)
                            startActivity(intent)
                            commonFuncs.hideLoadingDialog()
                            finish()
                        }

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