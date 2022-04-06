package com.example.funnytimesvendorapp.EditsSection

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.FTPClinicType
import com.example.funnytimesvendorapp.Models.FTPItemPhoto
import com.example.funnytimesvendorapp.Models.FTPNewService
import com.example.funnytimesvendorapp.RecViews.NewServicesRecView
import com.example.funnytimesvendorapp.RecViews.PropertyPhotoRecView
import com.example.funnytimesvendorapp.SpinnerAdapters.SClinicTypeAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenEditClinicBinding
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

class EditClinicScreen : AppCompatActivity() {


    val ftpClinicType = ArrayList<FTPClinicType>()
    val ftpPropPhotos = ArrayList<FTPItemPhoto>()
    val ftpNewServices = ArrayList<FTPNewService>()
    val commonFuncs = CommonFuncs()

    lateinit var newServicesRecView: NewServicesRecView
    lateinit var propertyPhotoRecView: PropertyPhotoRecView

    val client = OkHttpClient()


    var clinicid = ""
    var clinicname = ""
    var clinictype = ""
    var clinicdescription = ""
    var clinicdidservices = ""
    var clinicdidimage = ""

    lateinit var binding:FtpScreenEditClinicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenEditClinicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener {
            finish()
        }
        clinicid = intent.getStringExtra("ItemId").toString()
        propertyPhotoRecView = PropertyPhotoRecView(ftpPropPhotos,this)
        binding.ClinicPhotosRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.ClinicPhotosRecycler.adapter = propertyPhotoRecView
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
                clinicdidimage = "selected"
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

        binding.AddService.paintFlags = binding.AddService.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        newServicesRecView = NewServicesRecView(ftpNewServices,this)
        binding.ClinicServicesRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,
            false)
        binding.ClinicServicesRecycler.adapter = newServicesRecView
        binding.AddService.setOnClickListener {
            ftpNewServices.add(FTPNewService(null,"",""))
            Log.e("AfterDelete",ftpNewServices.toString())
            newServicesRecView.notifyDataSetChanged()
        }

        binding.ClinicAdd.setOnClickListener {
            clinicname = binding.ClinicName.text.toString()
            clinicdescription = binding.ClinicDesc.text.toString()
            if (clinicname.isNullOrEmpty()){
                binding.ClinicName.error = "لا يمكن ترك الحقل فارغ"
                binding.ClinicName.requestFocus()
                return@setOnClickListener
            }
            if (clinicdescription.isNullOrEmpty()){
                binding.ClinicDesc.error = "لا يمكن ترك الحقل فارغ"
                binding.ClinicDesc.requestFocus()
                return@setOnClickListener
            }
            if (newServicesRecView.GetServices().size == 0){
                Toast.makeText(this, "يجب عليك إضافة خدماتك", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Log.e("GetServices",newServicesRecView.GetServices().toString())
            Log.e("GetServices",newServicesRecView.CheckServiceIsEmpty().toString())
            if (newServicesRecView.CheckServiceIsEmpty()){
                Toast.makeText(this, "لا يمكنك ترك الحقول فارغة", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (ftpPropPhotos.size == 0){
                Toast.makeText(this, "يجب عليك اختيار صورة واحدة على الأقل", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Edit_Clinic_Request()
        }

        binding.ClinicDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من خيار الحذف ؟")
                .setPositiveButton("نعم") { _, _ ->
                    Delete_Clinic_Request()
                }
                .setNegativeButton("لا") { _, _ ->
                }
                .show()
        }

        Clinic_tools_Request()

    }

    fun Edit_Clinic_Request(){
        try {
            commonFuncs.showLoadingDialog(this)
            val multipartBody = MultipartBody.Builder()
            multipartBody.setType(MultipartBody.FORM)
            multipartBody.addFormDataPart("name", clinicname)
            multipartBody.addFormDataPart("description", clinicdescription)
            multipartBody.addFormDataPart("category_id", "2")
            multipartBody.addFormDataPart("sub_category_id", clinictype)
            multipartBody.addFormDataPart("_method", "PATCH")

            val services = newServicesRecView.GetServices()
            for (i in 0 until services.size){
                if (services[i].ServiceId == null){
                    multipartBody.addFormDataPart("services[service$i][name]", services[i].ServiceName.toString())
                    multipartBody.addFormDataPart("services[service$i][price]", services[i].ServicePrice.toString())
                }else{
                    var oldid = services[i].ServiceId
                    multipartBody.addFormDataPart("services_old[$oldid][name]", services[i].ServiceName.toString())
                    multipartBody.addFormDataPart("services_old[$oldid][price]", services[i].ServicePrice.toString())
                }

            }

            for(photo in ftpPropPhotos){
                if (photo.PhotoType == "new"){
                    val file1 = commonFuncs.getFileFromUri(photo.PhotoUri!!,this)
//                val file1 = File(getRealPath(photo.PhotoUri!!))
                    val fileRequestBody = file1!!.asRequestBody("image/jpg".toMediaType())
                    val imagename = System.currentTimeMillis().toString()
                    multipartBody.addFormDataPart("imgs[]", imagename, fileRequestBody)
                }
            }

            for (id in newServicesRecView.getDeletedServices()){
                multipartBody.addFormDataPart("services_deleted[]", id.toString())
            }

            for (id in propertyPhotoRecView.getDeletedPhotos()){
                multipartBody.addFormDataPart("deletedPhotos[]", id.toString())
            }


            val requestBody: RequestBody = multipartBody.build()
            val request: okhttp3.Request = okhttp3.Request.Builder()
                .addHeader("Authorization", "Bearer ${commonFuncs.GetFromSP(this,
                    Constants.KeyUserToken
                )}")
                .url(Constants.APIMain +"api/vendor-app/service/$clinicid")
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
                        commonFuncs.showDefaultDialog(this@EditClinicScreen,"فشل في العملية","حصل خطأ ما أثناء عملية الدفع , تأكد من اتصالك بالانترنت أو حاول مرة أخرى")
                    }
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    runOnUiThread {
                        Log.e("onResponse",response.toString())

                        Log.e("onResponse",response.message.toString())
                        commonFuncs.hideLoadingDialog()
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

    fun GetSetCurrentData() {
        Log.e("GetSetCurrentData", "GetSetCurrentData")
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/service/$clinicid"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val gallery = data.getJSONArray("gallery")
                    val services = data.getJSONArray("services")
                    val gson = GsonBuilder().create()
                    ftpPropPhotos.clear()
                    ftpPropPhotos.addAll(gson.fromJson(gallery.toString(),Array<FTPItemPhoto>::class.java).toList())
                    propertyPhotoRecView.notifyDataSetChanged()


                    ftpNewServices.addAll(gson.fromJson(services.toString(),Array<FTPNewService>::class.java).toList())
                    newServicesRecView.notifyDataSetChanged()

                    for (i in 0 until ftpClinicType.size){
                        if (ftpClinicType[i].TypeId == data.getInt("sub_category_id")){
                            binding.ClinicTypeSpinner.setSelection(i)
                        }
                    }
                    binding.ClinicName.setText(data.getString("name").toString())
                    binding.ClinicDesc.setText(data.getString("description").toString())



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
                    val map = java.util.HashMap<String, String>()
                    if (commonFuncs.IsInSP(this@EditClinicScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditClinicScreen, Constants.KeyUserToken)
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


    fun Clinic_tools_Request() {
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/create/2"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val subcategories = data.getJSONArray("subCategory")
                    val gson = GsonBuilder().create()
                    ftpClinicType.addAll(gson.fromJson(subcategories.toString(),Array<FTPClinicType>::class.java).toList())
                    val sClinicTypeAdapter = SClinicTypeAdapter(this,ftpClinicType)
                    binding.ClinicTypeSpinner.adapter = sClinicTypeAdapter
                    binding.ClinicTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            clinictype = sClinicTypeAdapter.getItem(position)!!.TypeId.toString()
                        }
                    }
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
                    if (commonFuncs.IsInSP(this@EditClinicScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditClinicScreen, Constants.KeyUserToken)
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

    fun Delete_Clinic_Request() {
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/service/$clinicid"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.DELETE, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    commonFuncs.hideLoadingDialog()
                    finish()
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
                    val map = java.util.HashMap<String, String>()
                    if (commonFuncs.IsInSP(this@EditClinicScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditClinicScreen, Constants.KeyUserToken)
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