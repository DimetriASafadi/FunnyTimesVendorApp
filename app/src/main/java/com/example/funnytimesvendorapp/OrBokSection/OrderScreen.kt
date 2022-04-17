package com.example.funnytimesvendorapp.OrBokSection

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
import com.example.funnytimesvendorapp.Models.FTPMyOrderItem
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.OrderItemsRecView
import com.example.funnytimesvendorapp.databinding.FtpScreenFoodBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class OrderScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenFoodBinding

    var itemid = ""

    val commonFuncs = CommonFuncs()


    val fTPMyOrderItem = ArrayList<FTPMyOrderItem>()
    lateinit var orderItemsRecView:OrderItemsRecView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenFoodBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        itemid = intent.getStringExtra("itemid").toString()

        binding.backBtn.setOnClickListener {
            finish()
        }


        orderItemsRecView = OrderItemsRecView(fTPMyOrderItem,this)
        binding.OrderItemsRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.OrderItemsRecycler.adapter = orderItemsRecView

        Order_Details_Request()
    }





    fun Order_Details_Request(){
        val url = Constants.APIMain + "api/vendor-app/order/$itemid"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val gson = GsonBuilder().create()
                    val id = data.getInt("id")
                    val total = data.getString("total")
                    val payment_status = data.getString("payment_status")
                    val store_name = data.getString("store_name")
                    val created_at = data.getString("created_at")
                    val status = data.getString("status")
                    val lat = data.getString("lat")
                    val lng = data.getString("lng")
                    val username = data.getString("username")
                    val payment_gateway = data.getString("payment_gateway")

                    val items = data.getJSONArray("items")
                    fTPMyOrderItem.addAll(gson.fromJson(items.toString(),Array<FTPMyOrderItem>::class.java).toList())
                    orderItemsRecView.notifyDataSetChanged()

                    binding.FoodOrderName.text = fTPMyOrderItem[0].ItemDetails?.ItemName.toString()
                    binding.FoodOrderId.text = "طلب رقم :$id"
                    binding.FoodCustomerName.text = username
                    binding.FoodCustomerLocation.text = lat+","+lng

                    Glide.with(this)
                        .load(fTPMyOrderItem[0].ItemDetails?.ItemImg.toString())
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.FoodImage)


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
                    if (commonFuncs.IsInSP(this@OrderScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@OrderScreen, Constants.KeyUserToken)
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