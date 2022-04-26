package com.example.funnytimesvendorapp.OrBokSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.CommonSection.Constants.GOOGLE_MAP_TEST_KEY
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
        commonFuncs.showLoadingDialog(this)
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    var nesteddata:JSONObject? = null
                    if (!data.isNull("data")){
                        nesteddata = data.getJSONObject("data")
//                        val deposit = nesteddata.getString("deposit")
                        val lat = nesteddata.getString("lat").toString()
                        val lng = nesteddata.getString("lng").toString()
                        val url = "https://maps.googleapis.com/maps/api/staticmap?size=1100x795&markers=color:red|label:S|$lat,$lng&key=$GOOGLE_MAP_TEST_KEY"
                        binding.ServiceCustomerLocation.text = nesteddata.getString("lat")+","+nesteddata.getString("lng")

                        Glide.with(this)
                            .load(url)
                            .centerCrop()
                            .placeholder(R.drawable.ft_broken_image)
                            .into(binding.ServiceImage)
                        Glide.with(this)
                            .load(nesteddata.getString("img"))
                            .centerCrop()
                            .placeholder(R.drawable.ft_broken_image)
                            .into(binding.ServiceImage)
                    }





                    val id = data.getInt("id")
                    val name = data.getString("name")
                    val type = data.getString("type")
                    val total = data.getInt("total")
                    val booking_type = data.getInt("booking_type")
                    val start_date = data.getString("start_date").toString()
                    val end_date = data.getString("end_date").toString()
                    val start_hour = data.getString("start_hour").toString()
                    val end_hour = data.getString("end_hour").toString()
                    var period = if (!data.isNull("period")) data.getInt("period") else ""
                    val username = data.getString("username")
                    val payment_gateway = data.getString("payment_gateway")


                    binding.ServiceOrderName.text = name
                    binding.ServiceOrderId.text = "طلب رقم :$id"
                    binding.ServiceCustomerName.text = username
                    binding.ServiceType.text = type
                    binding.ServicePrice.text = total.toString()
                    binding.ServiceDeposit.text = total.toString()
                    binding.ServicePeriod.text = period.toString()
                    binding.ServiceStartDate.text = start_date
                    binding.ServiceEndDate.text = end_date
                    binding.ServiceStartTime.text = start_hour
                    binding.ServiceEndTime.text = end_hour
                    binding.ServicePaymentMethod.text = payment_gateway


                    if (booking_type == 1){
                        binding.SectionStartDate.visibility = View.VISIBLE
                        binding.SectionEndDate.visibility = View.VISIBLE
                        binding.SectionStartTime.visibility = View.GONE
                        binding.SectionEndTime.visibility = View.GONE
                        binding.SectionPeriod.visibility = View.GONE

                    }else if (booking_type == 2){
                        binding.SectionStartDate.visibility = View.VISIBLE
                        binding.SectionEndDate.visibility = View.GONE
                        binding.SectionStartTime.visibility = View.VISIBLE
                        binding.SectionEndTime.visibility = View.VISIBLE
                        binding.SectionPeriod.visibility = View.GONE

                    }else if(booking_type == 3){
                        binding.SectionStartDate.visibility = View.VISIBLE
                        binding.SectionEndDate.visibility = View.GONE
                        binding.SectionStartTime.visibility = View.GONE
                        binding.SectionEndTime.visibility = View.GONE
                        binding.SectionPeriod.visibility = View.VISIBLE

                    }else if (booking_type == 4){
                        binding.SectionStartDate.visibility = View.VISIBLE
                        binding.SectionEndDate.visibility = View.GONE
                        binding.SectionStartTime.visibility = View.VISIBLE
                        binding.SectionEndTime.visibility = View.GONE
                        binding.SectionPeriod.visibility = View.GONE

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
            commonFuncs.hideLoadingDialog()
        }
    }
}