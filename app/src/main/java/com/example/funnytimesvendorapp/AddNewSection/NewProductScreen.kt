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
import com.example.funnytimesvendorapp.Models.FTPProductAttributeContainer
import com.example.funnytimesvendorapp.Models.FTPProductType
import com.example.funnytimesvendorapp.Models.FTPItemPhoto
import com.example.funnytimesvendorapp.RecViews.ProductAttrContainersRecView
import com.example.funnytimesvendorapp.RecViews.PropertyPhotoRecView
import com.example.funnytimesvendorapp.SpinnerAdapters.SProductTypeAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenNewProductBinding
import com.google.gson.GsonBuilder
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.HashMap

class NewProductScreen : AppCompatActivity() {

    val commonFuncs = CommonFuncs()
    val ftpProductType = ArrayList<FTPProductType>()
    val ftpPropPhotos = ArrayList<FTPItemPhoto>()
    val client = OkHttpClient()


    lateinit var binding:FtpScreenNewProductBinding
    lateinit var productAttrContainersRecView:ProductAttrContainersRecView

    var productname = ""
    var producttype = ""
    var productprice = ""
    var productquantity = ""
    var productdesc = ""
    var productdidimage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenNewProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val propertyPhotoRecView = PropertyPhotoRecView(ftpPropPhotos,this)
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
        binding.ProductAdd.setOnClickListener {
            productname = binding.ProductName.text.toString()
            productprice = binding.ProductPrice.text.toString()
            productquantity = binding.ProductQuantity.text.toString()
            productdesc = binding.ProductDesc.text.toString()

            if (productname.isNullOrEmpty()){
                binding.ProductName.error = "لا يمكن ترك الحقل فارغ"
                binding.ProductName.requestFocus()
                return@setOnClickListener
            }
            if (productprice.isNullOrEmpty()){
                binding.ProductPrice.error = "لا يمكن ترك الحقل فارغ"
                binding.ProductPrice.requestFocus()
                return@setOnClickListener
            }
            if (productquantity.isNullOrEmpty()){
                binding.ProductQuantity.error = "لا يمكن ترك الحقل فارغ"
                binding.ProductQuantity.requestFocus()
                return@setOnClickListener
            }
            if (productdesc.isNullOrEmpty()){
                binding.ProductDesc.error = "لا يمكن ترك الحقل فارغ"
                binding.ProductDesc.requestFocus()
                return@setOnClickListener
            }
            if (productdidimage.isNullOrEmpty()){
                Toast.makeText(this, "يجب عليك اختيار صورة واحدة على الأقل", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            Add_Product_Request()
        }



        Product_tools_Request()




    }



    fun Add_Product_Request(){
        try {
            commonFuncs.showLoadingDialog(this)
            val multipartBody = MultipartBody.Builder()
            multipartBody.setType(MultipartBody.FORM)
            multipartBody.addFormDataPart("name", productname)
            multipartBody.addFormDataPart("description", productdesc)
            multipartBody.addFormDataPart("category_id", "4")
            multipartBody.addFormDataPart("sub_category_id", producttype)
            multipartBody.addFormDataPart("stock", productquantity)
            multipartBody.addFormDataPart("price", productprice)

            productAttrContainersRecView.notifyDataSetChanged()
            for (container in productAttrContainersRecView.GetProductAttributes()){
                for (attribute in container.ContainerAttributes!!){
                    if (attribute.IsSelected){
                        multipartBody.addFormDataPart("attr[${container.ContainerId}][${attribute.AttributeId}]", "on")
                    }
                }
            }



            for(photo in ftpPropPhotos){
                val file1 = commonFuncs.getFileFromUri(photo.PhotoUri!!,this)
                val fileRequestBody = file1!!.asRequestBody("image/jpg".toMediaType())
                val imagename = System.currentTimeMillis().toString()
                multipartBody.addFormDataPart("imgs[]", imagename, fileRequestBody)
            }


            val requestBody: RequestBody = multipartBody.build()
            val request: okhttp3.Request = okhttp3.Request.Builder()
                .addHeader("Authorization", "Bearer ${commonFuncs.GetFromSP(this,
                    Constants.KeyUserToken
                )}")
                .url(Constants.APIMain +"api/vendor-app/shop/store")
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                @Throws(IOException::class)
                override fun onFailure(call: Call, e: java.io.IOException) {
                    runOnUiThread {
                        Log.e("onFailure","onFailure")
                        Log.e("onFailure",call.toString())
                        Log.e("onFailure",e.message.toString())
                        Log.e("onFailure",e.toString())
                        commonFuncs.hideLoadingDialog()
                        commonFuncs.showDefaultDialog(this@NewProductScreen,"فشل في العملية","حصل خطأ ما أثناء عملية الدفع , تأكد من اتصالك بالانترنت أو حاول مرة أخرى")
                    }
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    runOnUiThread {
                        Log.e("onResponse",response.message.toString())
                        Log.e("onResponse",response.toString())
                        Log.e("onResponse",response.code.toString())
                        commonFuncs.hideLoadingDialog()
                        finish()
                    }
                }
            })
        } catch (e: IOException) {
            Log.e("TryCatchFinal",e.message.toString()+"A7a")
            e.printStackTrace()
            commonFuncs.hideLoadingDialog()
            commonFuncs.showDefaultDialog(this,"خطأ في الاتصال","حصل خطأ ما")
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

                    val ftpProductAttributeContainer = ArrayList<FTPProductAttributeContainer>()
                    productAttrContainersRecView = ProductAttrContainersRecView(ftpProductAttributeContainer,this)
                    binding.ProductAttributesRecycler.layoutManager = LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
                    binding.ProductAttributesRecycler.adapter = productAttrContainersRecView



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
                    if (commonFuncs.IsInSP(this@NewProductScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@NewProductScreen, Constants.KeyUserToken)
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