package com.example.funnytimesvendorapp.MyProductSection

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.FTPMyItem
import com.example.funnytimesvendorapp.RecViews.MyItemsRecView
import com.example.funnytimesvendorapp.databinding.FtpScreenMyProductBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class MyProductScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenMyProductBinding
    val commonFuncs = CommonFuncs()

    val ftpMyItem = ArrayList<FTPMyItem>()
    lateinit var myItemsRecView:MyItemsRecView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenMyProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        myItemsRecView = MyItemsRecView(ftpMyItem,this)
        binding.MyItemsRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,
            false)
        binding.MyItemsRecycler.adapter = myItemsRecView


        get_myProducts_Request()


    }



    fun get_myProducts_Request(){
        val url = Constants.APIMain + "api/vendor-app/product/get"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
                    ftpMyItem.clear()
                    ftpMyItem.addAll(gson.fromJson(data.toString(),Array<FTPMyItem>::class.java).toList())
                    myItemsRecView.notifyDataSetChanged()

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
                    val map = HashMap<String,String>()
                    if (commonFuncs.IsInSP(this@MyProductScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@MyProductScreen, Constants.KeyUserToken)
                    }
                    return map
                }
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
        }
    }




}