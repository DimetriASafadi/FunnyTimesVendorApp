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
import com.example.funnytimesvendorapp.EditsSection.EditChaletScreen
import com.example.funnytimesvendorapp.EditsSection.EditFoodScreen
import com.example.funnytimesvendorapp.Models.FTAttribute
import com.example.funnytimesvendorapp.Models.FTItemPhoto
import com.example.funnytimesvendorapp.Models.FTReview
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.AttributesRecView
import com.example.funnytimesvendorapp.RecViews.ItemGalleryRecView
import com.example.funnytimesvendorapp.RecViews.ReviewRecView
import com.example.funnytimesvendorapp.databinding.FtScreenChaletBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class ChaletScreen : AppCompatActivity() {

    lateinit var binding: FtScreenChaletBinding
    val commonFuncs = CommonFuncs()

    var itemid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtScreenChaletBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        itemid = intent.getStringExtra("ItemId").toString()
        Log.e("ProId",itemid.toString())

        binding.ChaletBack.setOnClickListener {
            finish()
        }

        binding.ItemEditBtn.setOnClickListener {
            val intent = Intent(this, EditChaletScreen::class.java)
            intent.putExtra("ItemId",itemid)
            startActivity(intent)
        }


//        item_Request(this,itemid)
    }
    fun item_Request(activity: Activity, itemid :String){
        commonFuncs.showLoadingDialog(activity)
        val url = Constants.APIMain + "api/vendor-app/property/"+itemid
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")

                    binding.ChaletName.text = data.getString("name").toString()

                    Glide.with(this)
                        .load(data.getString("img").toString())
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.ChaletImage)
                    binding.ChaletCity.text = data.getString("address")
                    binding.ChaletDesc.text = data.getString("description")
                    binding.ChaletPrice.text = data.getString("price")+" ر.س"
                    binding.VendorPolicies.setOnClickListener {
                        commonFuncs.showDefaultDialog(activity,"الشروط والأحكام",data.getString("policy").toString())
                    }
                    binding.ChaletRating.rating = data.getString("star").toFloat()
                    binding.ChaletRating.setOnTouchListener { _, _ ->
                        return@setOnTouchListener true
                    }
                    val gson = GsonBuilder().create()
                    val ftPhotos = ArrayList<FTItemPhoto>()
                    val ftReviews = ArrayList<FTReview>()
                    val ftAttributes = ArrayList<FTAttribute>()
                    ftPhotos.addAll(gson.fromJson(data.getJSONArray("gallery").toString(),Array<FTItemPhoto>::class.java).toList())
                    ftReviews.addAll(gson.fromJson(data.getJSONArray("reviews").toString(),Array<FTReview>::class.java).toList())
                    ftAttributes.addAll(gson.fromJson(data.getJSONArray("AttrabiuteProperty").toString(),Array<FTAttribute>::class.java).toList())


                    val attributesRecView = AttributesRecView(ftAttributes,this)
                    binding.ChaletAttRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        false)
                    binding.ChaletAttRecycler.adapter = attributesRecView
                    val itemGalleryRecView = ItemGalleryRecView(ftPhotos,this)
                    binding.ChaletGalleryRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        false)
                    binding.ChaletGalleryRecycler.adapter = itemGalleryRecView
                    val reviewRecView = ReviewRecView(ftReviews,this)
                    binding.ChaletReviewRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
                    binding.ChaletReviewRecycler.adapter = reviewRecView


                    if (ftReviews.size == 0){
                        binding.ReviewsTitle.text = "لا يوجد تقييمات للعملاء"
                    }else{
                        binding.ReviewsTitle.text = "تقييمات العملاء"
                    }

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
                    if (commonFuncs.IsInSP(this@ChaletScreen, Constants.KeyUserToken)){
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