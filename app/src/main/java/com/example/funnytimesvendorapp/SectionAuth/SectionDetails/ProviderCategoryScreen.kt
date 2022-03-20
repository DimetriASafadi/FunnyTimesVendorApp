package com.example.funnytimesvendorapp.SectionAuth.SectionDetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.CommonSection.Constants.ProviderTools
import com.example.funnytimesvendorapp.InterFaces.OnCategoryClick
import com.example.funnytimesvendorapp.Models.FTPCategory
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.ProvCategoryRecView
import com.example.funnytimesvendorapp.databinding.FtpScreenProviderCategoryBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class ProviderCategoryScreen : AppCompatActivity(), OnCategoryClick {

    lateinit var binding:FtpScreenProviderCategoryBinding
    val commonFuncs = CommonFuncs()
    lateinit var provCategoryRecView:ProvCategoryRecView
    val ftpCategory = ArrayList<FTPCategory>()

    var phonenum = ""
    var temptoken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenProviderCategoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        phonenum = intent.getStringExtra("phonenum").toString()
        temptoken = intent.getStringExtra("temptoken").toString()

        provCategoryRecView = ProvCategoryRecView(ftpCategory,this,this)
        binding.ProCatsRecycler.layoutManager =  GridLayoutManager(this,2)
        binding.ProCatsRecycler.adapter = provCategoryRecView

        Provider_Request()


    }


    override fun OnCategoryClickListener(ftpCategory: FTPCategory) {
        val intent = Intent(this,ProviderLocationScreen::class.java)
        intent.putExtra("phonenum",phonenum)
        intent.putExtra("temptoken",temptoken)
        intent.putExtra("ftpCategory",ftpCategory)
        startActivity(intent)
    }


    fun Provider_Request(){
        var url = Constants.APIMain + "api/vendor-app/categories"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
                    ftpCategory.addAll(gson.fromJson(data.toString(),Array<FTPCategory>::class.java).toList())
                    provCategoryRecView.notifyDataSetChanged()
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
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params = HashMap<String,String>()
                    params["Authorization"] =  "Bearer $temptoken"
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
        }
    }




}