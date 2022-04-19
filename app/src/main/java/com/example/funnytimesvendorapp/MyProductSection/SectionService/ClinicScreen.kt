package com.example.funnytimesvendorapp.SectionService

import android.app.Activity
import android.content.Intent
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
import com.example.funnytimesvendorapp.EditsSection.EditClinicScreen
import com.example.funnytimesvendorapp.EditsSection.EditFoodScreen
import com.example.funnytimesvendorapp.Models.FTClinicService
import com.example.funnytimesvendorapp.Models.FTItemPhoto
import com.example.funnytimesvendorapp.Models.FTReview
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.ClinicServicesRecView
import com.example.funnytimesvendorapp.RecViews.ItemGalleryRecView
import com.example.funnytimesvendorapp.RecViews.ReviewRecView
import com.example.funnytimesvendorapp.databinding.FtScreenClinicBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class ClinicScreen : AppCompatActivity() {

    lateinit var binding: FtScreenClinicBinding
    val commonFuncs = CommonFuncs()
    var price = "0.0"

    var itemid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtScreenClinicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        itemid = intent.getStringExtra("ItemId").toString()
        Log.e("ProId",itemid.toString())

        binding.ClinicBack.setOnClickListener {
            finish()
        }

        binding.ItemEditBtn.setOnClickListener {
            val intent = Intent(this, EditClinicScreen::class.java)
            intent.putExtra("ItemId",itemid)
            startActivity(intent)
        }


//        item_Request(this,itemid)
    }
    fun item_Request(activity: Activity, itemid :String){
        commonFuncs.showLoadingDialog(activity)
        val url = Constants.APIMain + "api/vendor-app/service/"+itemid
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")

                    binding.ClinicName.text = data.getString("name").toString()

                    Glide.with(this)
                        .load(data.getString("img").toString())
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.ClinicImage)
                    binding.ClinicCity.text = data.getString("address")
                    binding.ClinicDesc.text = data.getString("description")
                    binding.ClinicRating.rating = data.getString("star").toFloat()
                    binding.ClinicRating.setOnTouchListener { _, _ ->
                        return@setOnTouchListener true
                    }
                    val gson = GsonBuilder().create()
                    val ftPhotos = ArrayList<FTItemPhoto>()
                    val ftReviews = ArrayList<FTReview>()
                    val ftServices = ArrayList<FTClinicService>()
                    ftPhotos.addAll(gson.fromJson(data.getJSONArray("gallery").toString(),Array<FTItemPhoto>::class.java).toList())
                    ftReviews.addAll(gson.fromJson(data.getJSONArray("reviews").toString(),Array<FTReview>::class.java).toList())
                    ftServices.addAll(gson.fromJson(data.getJSONArray("services").toString(),Array<FTClinicService>::class.java).toList())



                    val itemGalleryRecView = ItemGalleryRecView(ftPhotos,this)
                    binding.ClinicGalleryRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        false)
                    binding.ClinicGalleryRecycler.adapter = itemGalleryRecView
                    val reviewRecView = ReviewRecView(ftReviews,this)
                    binding.ClinicReviewsRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
                    binding.ClinicReviewsRecycler.adapter = reviewRecView

                    val clinicServicesRecView = ClinicServicesRecView(ftServices,this)
                    binding.ClinicServices.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
                    binding.ClinicServices.adapter = clinicServicesRecView



                    if (ftReviews.size == 0){
                        binding.ReviewsTitle.text = "لا يوجد تقييمات للعملاء"
                    }else{
                        binding.ReviewsTitle.text = "تقييمات العملاء"
                    }


//                    ClinicMapLocation





                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(activity,"خطأ في الاتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(activity,"خطأ في الاتصال","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String,String>()
                    if (commonFuncs.IsInSP(this@ClinicScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(activity, Constants.KeyUserToken)
                    }
                    return map
                }
            }
            val requestQueue = Volley.newRequestQueue(activity)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        item_Request(this,itemid)

    }


}