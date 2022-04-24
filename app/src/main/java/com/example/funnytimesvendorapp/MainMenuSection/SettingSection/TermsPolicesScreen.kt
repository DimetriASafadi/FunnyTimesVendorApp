package com.example.funnytimesvendorapp.MainMenuSection.SettingSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.databinding.TScreenTermsPolicesBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class TermsPolicesScreen : AppCompatActivity() {

    lateinit var binding:TScreenTermsPolicesBinding
    val commonFuncs = CommonFuncs()

    var type = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TScreenTermsPolicesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        type = intent.getStringExtra("type").toString()

        if (type == "term"){
            binding.PageTitle.text = "شروط الاستخدام"
            terms_Request()
        }
        if (type == "privacy"){
            binding.PageTitle.text = "سياسة الخصوصية"
            privacy_Request()
        }




    }

    fun terms_Request(){
        var url = Constants.APIMain + "api/vendor-app/terms"
        try {
            commonFuncs.showLoadingDialog(this)
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getString("data")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        var converthtml:String = Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY).toString()
                        binding.PageParagraph.text = converthtml
                    } else {
                        var converthtml:String = Html.fromHtml(data).toString()
                        binding.PageParagraph.text = converthtml
                    }



                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"خطأ في الاتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"خطأ في الاتصال","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {

            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }

    fun privacy_Request(){
        var url = Constants.APIMain + "api/vendor-app/privacy"
        try {
            commonFuncs.showLoadingDialog(this)
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getString("data")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        var converthtml:String = Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY).toString()
                        binding.PageParagraph.text = converthtml
                    } else {
                        var converthtml:String = Html.fromHtml(data).toString()
                        binding.PageParagraph.text = converthtml
                    }

                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"خطأ في الاتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"خطأ في الاتصال","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }
}