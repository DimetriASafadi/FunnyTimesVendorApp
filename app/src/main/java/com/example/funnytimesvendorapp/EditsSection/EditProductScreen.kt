package com.example.funnytimesvendorapp.EditsSection

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
import com.example.funnytimesvendorapp.Models.FTPProductAttributeContainer
import com.example.funnytimesvendorapp.Models.FTPProductType
import com.example.funnytimesvendorapp.Models.FTPItemPhoto
import com.example.funnytimesvendorapp.RecViews.ProductAttrContainersRecView
import com.example.funnytimesvendorapp.RecViews.PropertyPhotoRecView
import com.example.funnytimesvendorapp.SpinnerAdapters.SProductTypeAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenEditProductBinding
import com.google.gson.GsonBuilder
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.HashMap

class EditProductScreen : AppCompatActivity() {

    val commonFuncs = CommonFuncs()
    val ftpProductType = ArrayList<FTPProductType>()
    val ftpPropPhotos = ArrayList<FTPItemPhoto>()
    val ftpProductAttributeContainer = ArrayList<FTPProductAttributeContainer>()
    val client = OkHttpClient()

    lateinit var productAttrContainersRecView: ProductAttrContainersRecView
    lateinit var propertyPhotoRecView: PropertyPhotoRecView
    lateinit var binding:FtpScreenEditProductBinding

    var productid = ""
    var productname = ""
    var producttype = ""
    var productprice = ""
    var productquantity = ""
    var productdesc = ""
    var productdidimage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenEditProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        productid = intent.getStringExtra("ItemId").toString()

        productAttrContainersRecView = ProductAttrContainersRecView(ftpProductAttributeContainer,this)
        binding.ProductAttributesRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,
            false)
        binding.ProductAttributesRecycler.adapter = productAttrContainersRecView

        propertyPhotoRecView = PropertyPhotoRecView(ftpPropPhotos,this)
        binding.ProductPhotosRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.ProductPhotosRecycler.adapter = propertyPhotoRecView
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
                productdidimage = "selected"
                for (i in 0 until images.size) {
                    if (ftpPropPhotos.size < 20){
//                        Log.e("FTPPropPhoto",getRealPath(images[i].uri))
                        Log.e("FTPPropPhoto",images[i].uri.path.toString())
                        Log.e("FTPPropPhoto",images[i].uri.toString())
                        Log.e("FTPPropPhoto",images[i].bucketName)
                        Log.e("FTPPropPhoto",images[i].name)
                        Log.e("FTPPropPhoto",images[i].bucketId.toString())
                        ftpPropPhotos.add(FTPItemPhoto(null,"new",images[i].uri.toString(),images[i].uri))
                    }else{
                        Toast.makeText(this, "لقد وصلت الحد الأعلى للصور", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
                propertyPhotoRecView.notifyDataSetChanged()
            }
        }
        binding.PickImages.setOnClickListener {
            launcher.launch(config)
        }



        Product_tools_Request()

    }

    fun GetSetCurrentData() {
        Log.e("GetSetCurrentData", "GetSetCurrentData")
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/shop/$productid"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val gallery = data.getJSONArray("gallery")
                    val gson = GsonBuilder().create()
                    ftpPropPhotos.clear()
                    ftpPropPhotos.addAll(gson.fromJson(gallery.toString(),Array<FTPItemPhoto>::class.java).toList())
                    propertyPhotoRecView.notifyDataSetChanged()

                    for (i in 0 until ftpProductType.size){
                        if (ftpProductType[i].TypeId == data.getInt("sub_category_id")){
                            binding.ProductTypeSpinner.setSelection(i)
                        }
                    }
                    binding.ProductName.setText(data.getString("name").toString())
                    binding.ProductPrice.setText(data.getString("price").toString())
                    binding.ProductQuantity.setText(data.getString("stock").toString())
                    binding.ProductDesc.setText(data.getString("description").toString())

                    val AttrabiuteProperty = data.getJSONArray("AttrabiuteProperty")


                    val sProductTypeAdapter = SProductTypeAdapter(this,ftpProductType)
                    binding.ProductTypeSpinner.adapter = sProductTypeAdapter
                    binding.ProductTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            producttype = sProductTypeAdapter.getItem(position)!!.TypeId.toString()
                            ftpProductAttributeContainer.clear()
                            ftpProductAttributeContainer.addAll(sProductTypeAdapter.getItem(position)!!.TypeAttrContianers!!)
                            for (i in 0 until AttrabiuteProperty.length()){
                                val attribObj = AttrabiuteProperty.getJSONObject(i)
                                for (c in 0 until ftpProductAttributeContainer.size ){
                                    for (a in 0 until ftpProductAttributeContainer[c].ContainerAttributes!!.size ){
                                        if (attribObj.getInt("attribute_value_id") == ftpProductAttributeContainer[c].ContainerAttributes!![a].AttributeId){
                                            ftpProductAttributeContainer[c].ContainerAttributes!![a].IsSelected = true
                                        }
                                    }
                                }
                            }
                            productAttrContainersRecView.notifyDataSetChanged()
                        }
                    }

                    productAttrContainersRecView.notifyDataSetChanged()



                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"فشل الإتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"فشل الإتصال","تفقد إتصالك بالشبكة")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String,String>()
                    if (commonFuncs.IsInSP(this@EditProductScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditProductScreen, Constants.KeyUserToken)
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


    fun Product_tools_Request() {
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/create/4"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val subcategories = data.getJSONArray("subCategory")
                    val cities = data.getJSONArray("cities")
                    val gson = GsonBuilder().create()
                    ftpProductType.addAll(gson.fromJson(subcategories.toString(),Array<FTPProductType>::class.java).toList())

                    commonFuncs.hideLoadingDialog()
                    GetSetCurrentData()

                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"فشل الإتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"فشل الإتصال","تفقد إتصالك بالشبكة")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String,String>()
                    if (commonFuncs.IsInSP(this@EditProductScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditProductScreen, Constants.KeyUserToken)
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