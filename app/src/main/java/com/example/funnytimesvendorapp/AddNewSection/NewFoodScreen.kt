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
import com.example.funnytimesvendorapp.Models.FTPClinicType
import com.example.funnytimesvendorapp.Models.FTPFoodType
import com.example.funnytimesvendorapp.Models.FTPPropPhoto
import com.example.funnytimesvendorapp.RecViews.PropertyPhotoRecView
import com.example.funnytimesvendorapp.SpinnerAdapters.SClinicTypeAdapter
import com.example.funnytimesvendorapp.SpinnerAdapters.SFoodTypeAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenNewFoodBinding
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
import java.util.HashMap

class NewFoodScreen : AppCompatActivity() {


    val ftpFoodType = ArrayList<FTPFoodType>()
    val ftpPropPhotos = ArrayList<FTPPropPhoto>()
    val commonFuncs = CommonFuncs()
    lateinit var binding:FtpScreenNewFoodBinding

    var foodname = ""
    var foodtype = ""
    var foodprice = ""
    var fooddesc = ""
    var fooddidimage = ""

    val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenNewFoodBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val propertyPhotoRecView = PropertyPhotoRecView(ftpPropPhotos,this)
        binding.FoodPhotosRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.FoodPhotosRecycler.adapter = propertyPhotoRecView
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
                fooddidimage = "selected"
                for (i in 0 until images.size) {
                    if (ftpPropPhotos.size < 20){
//                        Log.e("FTPPropPhoto",getRealPath(images[i].uri))
                        Log.e("FTPPropPhoto",images[i].uri.path.toString())
                        Log.e("FTPPropPhoto",images[i].uri.toString())
                        Log.e("FTPPropPhoto",images[i].bucketName)
                        Log.e("FTPPropPhoto",images[i].name)
                        Log.e("FTPPropPhoto",images[i].bucketId.toString())
                        ftpPropPhotos.add(FTPPropPhoto(null,"new",images[i].uri.toString(),images[i].uri))
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

        binding.FoodAdd.setOnClickListener {
            foodname = binding.FoodName.text.toString()
            foodprice = binding.FoodPrice.text.toString()
            fooddesc = binding.FoodDesc.text.toString()
            if (foodname.isNullOrEmpty()){
                binding.FoodName.error = "لا يمكن ترك الحقل فارغ"
                binding.FoodName.requestFocus()
                return@setOnClickListener
            }
            if (foodprice.isNullOrEmpty()){
                binding.FoodPrice.error = "لا يمكن ترك الحقل فارغ"
                binding.FoodPrice.requestFocus()
                return@setOnClickListener
            }
            if (fooddesc.isNullOrEmpty()){
                binding.FoodDesc.error = "لا يمكن ترك الحقل فارغ"
                binding.FoodDesc.requestFocus()
                return@setOnClickListener
            }
            if (fooddidimage.isNullOrEmpty()){
                Toast.makeText(this, "يجب عليك اختيار صورة واحدة على الأقل", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Add_Food_Request()
        }

        Food_tools_Request()

    }

    fun Add_Food_Request(){
        try {
            commonFuncs.showLoadingDialog(this)
            val multipartBody = MultipartBody.Builder()
            multipartBody.setType(MultipartBody.FORM)
            multipartBody.addFormDataPart("name", foodname)
            multipartBody.addFormDataPart("description", fooddesc)
            multipartBody.addFormDataPart("category_id", "3")
            multipartBody.addFormDataPart("sub_category_id", foodtype)
            multipartBody.addFormDataPart("price", foodprice)

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
                .url(Constants.APIMain +"api/vendor-app/food/store")
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
                        commonFuncs.showDefaultDialog(this@NewFoodScreen,"فشل في العملية","حصل خطأ ما أثناء عملية الدفع , تأكد من اتصالك بالانترنت أو حاول مرة أخرى")
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


    fun Food_tools_Request() {
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/create/3"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val subcategories = data.getJSONArray("subCategory")
                    val gson = GsonBuilder().create()
                    ftpFoodType.addAll(gson.fromJson(subcategories.toString(),Array<FTPFoodType>::class.java).toList())
                    val sFoodTypeAdapter = SFoodTypeAdapter(this,ftpFoodType)
                    binding.FoodTypeSpinner.adapter = sFoodTypeAdapter
                    binding.FoodTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            foodtype = sFoodTypeAdapter.getItem(position)!!.TypeId.toString()
                        }
                    }
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
                    if (commonFuncs.IsInSP(this@NewFoodScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@NewFoodScreen, Constants.KeyUserToken)
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