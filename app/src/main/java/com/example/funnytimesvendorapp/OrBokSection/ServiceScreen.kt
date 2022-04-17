package com.example.funnytimesvendorapp.OrBokSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.FTPMyOrderItem
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.databinding.FtpScreenServiceBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class ServiceScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenServiceBinding
    var itemid = ""

    val commonFuncs = CommonFuncs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenServiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        itemid = intent.getStringExtra("itemid").toString()

        Service_Details_Request()

    }

    fun Service_Details_Request(){
        val url = Constants.APIMain + "api/vendor-app/booking/$itemid"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val nesteddata = data.getJSONObject("data")
                    val gson = GsonBuilder().create()
                    val id = data.getInt("id")
                    val name = data.getString("name")
                    val type = data.getString("type")
                    val total = data.getInt("total")
                    val price = nesteddata.getString("price")
                    val booking_type = data.getInt("booking_type")

                    val username = data.getString("username")
                    val payment_gateway = data.getString("payment_gateway")


                    binding.ServiceOrderName.text = name
                    binding.ServiceOrderId.text = "طلب رقم :$id"
                    binding.ServiceCustomerName.text = username
                    binding.ServiceCustomerLocation.text = nesteddata.getString("lat")+","+nesteddata.getString("lng")
                    binding.ServiceType.text = type
                    binding.ServicePrice.text = total.toString()
                    binding.ServiceDeposit.text = price

                    if (booking_type == 1){

                    }else if (booking_type == 2){

                    }else if(booking_type == 3){

                    }else if (booking_type == 4){


                    }


                    Glide.with(this)
                        .load(nesteddata.getString("img"))
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.ServiceImage)

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
                    if (commonFuncs.IsInSP(this@ServiceScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@ServiceScreen, Constants.KeyUserToken)
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