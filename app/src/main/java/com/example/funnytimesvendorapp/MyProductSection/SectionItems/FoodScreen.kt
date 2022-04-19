package com.example.funnytimesvendorapp.SectionItems

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
import com.example.funnytimesvendorapp.Models.FTItemPhoto
import com.example.funnytimesvendorapp.Models.FTReview
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.EditsSection.EditFoodScreen
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.ItemGalleryRecView
import com.example.funnytimesvendorapp.RecViews.ReviewRecView
import com.example.funnytimesvendorapp.SectionService.ChaletScreen
import com.example.funnytimesvendorapp.databinding.FtScreenFoodBinding

import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class FoodScreen : AppCompatActivity() {

    lateinit var binding: FtScreenFoodBinding
    val commonFuncs = CommonFuncs()
    val itemFuncs = ItemsFuncs()
    var itemid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtScreenFoodBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        itemid = intent.getStringExtra("ItemId").toString()
        Log.e("ProId",itemid.toString())

        binding.FoodBack.setOnClickListener {
            finish()
        }

        binding.ItemEditBtn.setOnClickListener {
            val intent = Intent(this, EditFoodScreen::class.java)
            intent.putExtra("ItemId",itemid)
            startActivity(intent)
        }

//        item_Request(this,itemid)
    }
    fun item_Request(activity: Activity,itemid :String){
        commonFuncs.showLoadingDialog(activity)
        val url = Constants.APIMain + "api/vendor-app/food/"+itemid
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val vendor = data.getJSONObject("vendor")

                    binding.FoodName.text = data.getString("name").toString()
                    binding.FoodFavorite.setOnClickListener {
                    }
                    Glide.with(this)
                        .load(data.getString("img").toString())
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.FoodImage)
                    Glide.with(this)
                        .load(vendor.getString("img").toString())
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.VendorImage)
                    binding.FoodCity.text = data.getString("address")
                    binding.FoodVendorName.text = vendor.getString("name")
                    val vendortype = data.getString("type")
                    val vendorid = vendor.getInt("id")

                    binding.FoodDesc.text = data.getString("description")
                    binding.FoodRating.rating = data.getString("star").toFloat()
                    binding.FoodRating.setOnTouchListener { _, _ ->
                        return@setOnTouchListener true
                    }
                    binding.FoodPrice.text = data.getString("price")+" ر.س"
                    val gson = GsonBuilder().create()
                    val ftPhotos = ArrayList<FTItemPhoto>()
                    val ftReviews = ArrayList<FTReview>()
                    ftPhotos.addAll(gson.fromJson(data.getJSONArray("gallery").toString(),Array<FTItemPhoto>::class.java).toList())
                    ftReviews.addAll(gson.fromJson(data.getJSONArray("reviews").toString(),Array<FTReview>::class.java).toList())

                    val itemGalleryRecView = ItemGalleryRecView(ftPhotos,this)
                    binding.FoodGalleryRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        false)
                    binding.FoodGalleryRecycler.adapter = itemGalleryRecView
                    val reviewRecView = ReviewRecView(ftReviews,this)
                    binding.FoodReviewsRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
                    binding.FoodReviewsRecycler.adapter = reviewRecView

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
                    if (commonFuncs.IsInSP(this@FoodScreen, Constants.KeyUserToken)){
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