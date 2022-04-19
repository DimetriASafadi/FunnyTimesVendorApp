package com.example.funnytimesvendorapp.SectionItems

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.EditsSection.EditFoodScreen
import com.example.funnytimesvendorapp.EditsSection.EditProductScreen
import com.example.funnytimesvendorapp.Models.FTItemPhoto
import com.example.funnytimesvendorapp.Models.FTProAttrContainer
import com.example.funnytimesvendorapp.Models.FTProAttribute
import com.example.funnytimesvendorapp.Models.FTReview
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.ItemGalleryRecView
import com.example.funnytimesvendorapp.RecViews.ProAttrContainerRecView
import com.example.funnytimesvendorapp.RecViews.ReviewRecView
import com.example.funnytimesvendorapp.databinding.FtScreenProductBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class ProductScreen : AppCompatActivity() {

    lateinit var binding: FtScreenProductBinding
    val commonFuncs = CommonFuncs()
    val itemFuncs = ItemsFuncs()
    var itemid = ""

    lateinit var proAttrContainerRecView : ProAttrContainerRecView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtScreenProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        itemid = intent.getStringExtra("ItemId").toString()
        Log.e("ProId",itemid.toString())

        binding.ProductBack.setOnClickListener {
            finish()
        }
        binding.ItemEditBtn.setOnClickListener {
            val intent = Intent(this, EditProductScreen::class.java)
            intent.putExtra("ItemId",itemid)
            startActivity(intent)
        }
//        item_Request(this,itemid)
    }
    fun item_Request(activity: Activity, itemid :String){
        commonFuncs.showLoadingDialog(activity)
        val url = Constants.APIMain + "api/vendor-app/shop/"+itemid
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val vendor = data.getJSONObject("vendor")

                    binding.ProductName.text = data.getString("name").toString()

                    Glide.with(this)
                        .load(data.getString("img").toString())
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.ProductImage)
                    Glide.with(this)
                        .load(vendor.getString("img").toString())
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.VendorImage)
                    binding.ProductCity.text = data.getString("address")
                    binding.ProductVendorName.text = vendor.getString("name")
                    val vendortype = data.getString("type")
                    val vendorid = vendor.getInt("id")

                    binding.ProductDesc.text = data.getString("description")
                    binding.ProductRating.rating = data.getString("star").toFloat()
                    binding.ProductRating.setOnTouchListener { _, _ ->
                        return@setOnTouchListener true
                    }
                    binding.ProductPrice.text = data.getString("price")
                    val gson = GsonBuilder().create()
                    val ftPhotos = ArrayList<FTItemPhoto>()
                    val ftReviews = ArrayList<FTReview>()
                    val ftProAttrContainer = ArrayList<FTProAttrContainer>()
                    val attributes = data.getJSONArray("attributes")
                    for (a in 0 until attributes.length())
                    {
                        val ftproAttributes = ArrayList<FTProAttribute>()
                        ftproAttributes.addAll(gson.fromJson(attributes.getJSONObject(a).getJSONArray("data").toString(),Array<FTProAttribute>::class.java).toList())
                        ftProAttrContainer.add(FTProAttrContainer(attributes.getJSONObject(a).getString("name").toString(),ftproAttributes))
                    }

                    ftPhotos.addAll(gson.fromJson(data.getJSONArray("gallery").toString(),Array<FTItemPhoto>::class.java).toList())
                    ftReviews.addAll(gson.fromJson(data.getJSONArray("reviews").toString(),Array<FTReview>::class.java).toList())


                    proAttrContainerRecView = ProAttrContainerRecView(ftProAttrContainer,this)
                    binding.ProAttrsRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
                    binding.ProAttrsRecycler.adapter = proAttrContainerRecView

                    val itemGalleryRecView = ItemGalleryRecView(ftPhotos,this)
                    binding.ProductGalleryRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        false)
                    binding.ProductGalleryRecycler.adapter = itemGalleryRecView
                    val reviewRecView = ReviewRecView(ftReviews,this)
                    binding.ProductReviewsRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
                    binding.ProductReviewsRecycler.adapter = reviewRecView

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
                    if (commonFuncs.IsInSP(this@ProductScreen, Constants.KeyUserToken)){
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