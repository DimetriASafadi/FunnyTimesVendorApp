package com.example.funnytimesvendorapp.AddNewSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.*
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.PropertyAttrContainersRecView
import com.example.funnytimesvendorapp.RecViews.PropertyPhotoRecView
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertyBookSpinner
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertyCityAdapter
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertySubCatAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenNewChaletBinding
import com.google.gson.GsonBuilder
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class NewChaletScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenNewChaletBinding
    val commonFuncs = CommonFuncs()

    val ftpPropertyBook = ArrayList<FTPPropertyBook>()
    val ftpPropertySubCat = ArrayList<FTPPropertySubCat>()
    val ftpPropertyCity = ArrayList<FTPPropertyCity>()
    val ftpPropPhotos = ArrayList<FTPPropPhoto>()

    var priceType = "static"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenNewChaletBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.StaticButton.setOnClickListener {
            binding.StaticButton.setBackgroundResource(R.drawable.ft_radius_fill)
            binding.StaticText.setTextColor(resources.getColor(R.color.ft_dark_blue,null))
            binding.DynamicButton.setBackgroundResource(0)
            binding.DynamicText.setTextColor(resources.getColor(R.color.ft_grey_1,null))
            priceType = "static"
        }
        binding.DynamicButton.setOnClickListener {
            binding.DynamicButton.setBackgroundResource(R.drawable.ft_radius_fill)
            binding.DynamicText.setTextColor(resources.getColor(R.color.ft_dark_blue,null))
            binding.StaticButton.setBackgroundResource(0)
            binding.StaticText.setTextColor(resources.getColor(R.color.ft_grey_1,null))
            priceType = "dynamic"

        }

        ftpPropertyBook.add(FTPPropertyBook(1,"أيام"))
        ftpPropertyBook.add(FTPPropertyBook(2,"ساعات"))
        ftpPropertyBook.add(FTPPropertyBook(3,"فترات"))

        val propertyPhotoRecView = PropertyPhotoRecView(ftpPropPhotos,this)
        binding.PropertyPhotosRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.PropertyPhotosRecycler.adapter = propertyPhotoRecView
        val config = ImagePickerConfig(
            statusBarColor = "#F9A236",
            isLightStatusBar = true,
            isFolderMode = true,
            isMultipleMode = true,
            maxSize = 20,
            subDirectory = "Photos",
            // See more at configuration attributes table below
        )
        val launcher = registerImagePicker { images ->
            if(images.isNotEmpty()){
                for (i in 0 until images.size) {
                    if (ftpPropPhotos.size < 20){
                        ftpPropPhotos.add(FTPPropPhoto(null,"new",images[i].uri.toString(),images[i].uri))
                    }else{
                        Toast.makeText(this, "لقد وصلت الحد الأعلى للصور", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
                propertyPhotoRecView.notifyDataSetChanged()
            }
        }
        binding.PropertyPickImage.setOnClickListener {
            launcher.launch(config)

        }

        Chalet_tools_Request()

    }

    fun Chalet_tools_Request() {
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/create/1"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val subcategories = data.getJSONArray("subCategory")
                    val cities = data.getJSONArray("cities")
                    val gson = GsonBuilder().create()
                    ftpPropertySubCat.addAll(gson.fromJson(subcategories.toString(),Array<FTPPropertySubCat>::class.java).toList())
                    ftpPropertyCity.addAll(gson.fromJson(cities.toString(),Array<FTPPropertyCity>::class.java).toList())

                    val ftpPropertyAttributeContainer = ArrayList<FTPPropertyAttributeContainer>()
                    val propertyAttrContainersRecView = PropertyAttrContainersRecView(ftpPropertyAttributeContainer,this)
                    binding.PropertyAttributesRecycler.layoutManager = LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL,
                    false)
                    binding.PropertyAttributesRecycler.adapter = propertyAttrContainersRecView



                    val sPropertyBookSpinner = SPropertyBookSpinner(this,ftpPropertyBook)
                    binding.PropertyBookingSpinner.adapter =sPropertyBookSpinner
                    binding.PropertyBookingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                        }
                    }


                    val sPropertySubCatAdapter = SPropertySubCatAdapter(this,ftpPropertySubCat)
                    binding.PropertyServiceSpinner.adapter = sPropertySubCatAdapter
                    binding.PropertyServiceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            ftpPropertyAttributeContainer.clear()
                            ftpPropertyAttributeContainer.addAll(sPropertySubCatAdapter.getItem(position)!!.SubCatAttrContainers!!)
                            propertyAttrContainersRecView.notifyDataSetChanged()
                        }
                    }
                    val sPropertyCityAdapter = SPropertyCityAdapter(this,ftpPropertyCity)
                    binding.PropertyCitySpinner.adapter = sPropertyCityAdapter
                    binding.PropertyCitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                     //       ftpPropertyAttributeContainer.clear()
                        }
                    }


                    propertyAttrContainersRecView.notifyDataSetChanged()
                    commonFuncs.hideLoadingDialog()

                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"فشل تسجيل الدخول",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"فشل تسجيل الدخول","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String,String>()
                    if (commonFuncs.IsInSP(this@NewChaletScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@NewChaletScreen, Constants.KeyUserToken)
                        Log.e("HomeToken",commonFuncs.GetFromSP(this@NewChaletScreen, Constants.KeyUserToken).toString())
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