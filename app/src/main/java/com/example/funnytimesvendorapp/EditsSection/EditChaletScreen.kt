package com.example.funnytimesvendorapp.EditsSection

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.TextView
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
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertyCityAdapter
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertySubCatAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenEditChaletBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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
import java.util.*

class EditChaletScreen : AppCompatActivity(), OnMapReadyCallback {


    lateinit var binding:FtpScreenEditChaletBinding
    val commonFuncs = CommonFuncs()

    val ftpPropertySubCat = ArrayList<FTPPropertySubCat>()
    val ftpPropertyCity = ArrayList<FTPPropertyCity>()
    val ftpPropPhotos = ArrayList<FTPPropPhoto>()
    val ftpPropertyAttributeContainer = ArrayList<FTPPropertyAttributeContainer>()

    lateinit var propertyPhotoRecView:PropertyPhotoRecView
    lateinit var propertyAttrContainersRecView:PropertyAttrContainersRecView
    lateinit var sPropertySubCatAdapter:SPropertySubCatAdapter


    var propid = ""
    var propname = ""
    var propdescription = ""
    var proppolicy = ""
    var proptype = ""
    var propcity = ""
    var propdistric = ""
    var propselectedlat = ""
    var propselectedlng = ""

    var mapDialog: Dialog? = null
    lateinit var googleMap: GoogleMap
    var marker: Marker? = null

    val client = OkHttpClient()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenEditChaletBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        propid = intent.getStringExtra("PropId").toString()
        propertyPhotoRecView = PropertyPhotoRecView(ftpPropPhotos,this)
        SetUpMapDialog()
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.PropMapFrag) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        binding.PropertyShowMap.setOnClickListener {
            mapDialog?.show()
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.PropertyPhotosRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.PropertyPhotosRecycler.adapter = propertyPhotoRecView

        propertyAttrContainersRecView = PropertyAttrContainersRecView(ftpPropertyAttributeContainer,this)
        binding.PropertyAttributesRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,
            false)
        binding.PropertyAttributesRecycler.adapter = propertyAttrContainersRecView


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

        Chalet_tools_Request()

        binding.PropertyEdit.setOnClickListener {

            propname = binding.PropertyName.text.toString()
            propdescription = binding.PropertyDesc.text.toString()
            proppolicy = binding.PropertyPolicy.text.toString()
            propdistric = binding.PropertyDistric.text.toString()

            if (propname.isNullOrEmpty()){
                binding.PropertyName.error = "لا يمكن ترك الحقل فارغ"
                binding.PropertyName.requestFocus()
                return@setOnClickListener
            }
            if (propdescription.isNullOrEmpty()){
                binding.PropertyDesc.error = "لا يمكن ترك الحقل فارغ"
                binding.PropertyDesc.requestFocus()
                return@setOnClickListener
            }
            if (proppolicy.isNullOrEmpty()){
                binding.PropertyPolicy.error = "لا يمكن ترك الحقل فارغ"
                binding.PropertyPolicy.requestFocus()
                return@setOnClickListener
            }
            if (propdistric.isNullOrEmpty()){
                binding.PropertyDistric.error = "لا يمكن ترك الحقل فارغ"
                binding.PropertyDistric.requestFocus()
                return@setOnClickListener
            }

            if (propertyAttrContainersRecView.CheckValuesEmpty()){
                Toast.makeText(this, "لا يجب عليك ترك أي قيم فارغة في خصائص العقار", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if (ftpPropPhotos.size == 0){
                Toast.makeText(this, "يجب عليك اختيار صورة واحدة على الأقل لعقارك", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Edit_Chalet_Request()
        }


    }

    fun Edit_Chalet_Request(){
        try {
            commonFuncs.showLoadingDialog(this)
            val multipartBody = MultipartBody.Builder()
            multipartBody.setType(MultipartBody.FORM)
            multipartBody.addFormDataPart("name", propname)
            multipartBody.addFormDataPart("description", propdescription)
            multipartBody.addFormDataPart("policy", proppolicy)
            multipartBody.addFormDataPart("sub_category_id", proptype)
            multipartBody.addFormDataPart("city_id", propcity)
            multipartBody.addFormDataPart("address", propdistric)
            multipartBody.addFormDataPart("lat", propselectedlat)
            multipartBody.addFormDataPart("lng", propselectedlng)
            multipartBody.addFormDataPart("_method", "PATCH")

            for(photo in ftpPropPhotos){
                if (photo.PhotoType == "new"){
                    val file1 = commonFuncs.getFileFromUri(photo.PhotoUri!!,this)
//                val file1 = File(getRealPath(photo.PhotoUri!!))
                    val fileRequestBody = file1!!.asRequestBody("image/jpg".toMediaType())
                    val imagename = System.currentTimeMillis().toString()
                    multipartBody.addFormDataPart("imgs[]", imagename, fileRequestBody)
                }
            }

            for (id in propertyPhotoRecView.getDeletedPhotos()){
                multipartBody.addFormDataPart("deletedPhotos[]", id.toString())
            }

            for (container in propertyAttrContainersRecView.GetAttributesContainer()){
                for (attribute in container.ContainerAttributes!!){
                    if (attribute.IsSelected){
                        Log.e("attribute",attribute.toString())
                        if (attribute.AttributeType == "value"){
                            multipartBody.addFormDataPart("attr[${container.ContainerId}][${attribute.AttributeId}]", attribute.AttributeValue.toString())
                        }else{
                            multipartBody.addFormDataPart("attr[${container.ContainerId}][${attribute.AttributeId}]", "on")
                        }
                    }
                }
            }

            val requestBody: RequestBody = multipartBody.build()
            val request: okhttp3.Request = okhttp3.Request.Builder()
                .addHeader("Authorization", "Bearer ${commonFuncs.GetFromSP(this,
                    Constants.KeyUserToken
                )}")
                .url(Constants.APIMain +"api/vendor-app/property/$propid")
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
                        commonFuncs.showDefaultDialog(this@EditChaletScreen,"فشل في العملية","حصل خطأ ما أثناء عملية الدفع , تأكد من اتصالك بالانترنت أو حاول مرة أخرى")
                    }
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    runOnUiThread {
                        Log.e("onResponse",response.toString())

                        Log.e("onResponse",response.message.toString())
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
                            propcity = sPropertyCityAdapter.getItem(position)!!.CityId.toString()
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
                    if (commonFuncs.IsInSP(this@EditChaletScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditChaletScreen, Constants.KeyUserToken)
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
    fun GetSetCurrentData(){
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/property/$propid"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val gallery = data.getJSONArray("gallery")
                    val gson = GsonBuilder().create()
                    ftpPropPhotos.clear()
                    ftpPropPhotos.addAll(gson.fromJson(gallery.toString(),Array<FTPPropPhoto>::class.java).toList())
                    propertyPhotoRecView.notifyDataSetChanged()


                    val AttrabiuteProperty = data.getJSONArray("AttrabiuteProperty")

                    binding.PropertyName.setText(data.getString("name").toString())
                    binding.PropertyDesc.setText(data.getString("description").toString())
                    binding.PropertyPolicy.setText(data.getString("policy").toString())
                    binding.PropertyDistric.setText(data.getString("address").toString())
                    propselectedlat = data.getString("lat").toString()
                    propselectedlng = data.getString("lng").toString()
                    for (i in 0 until ftpPropertySubCat.size){
                        if (ftpPropertySubCat[i].SubCatId == data.getInt("sub_category_id")){
                            binding.PropertyServiceSpinner.setSelection(i)
                        }
                    }

                    sPropertySubCatAdapter = SPropertySubCatAdapter(this,ftpPropertySubCat)
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
                            proptype = sPropertySubCatAdapter.getItem(position)!!.SubCatId.toString()
                            ftpPropertyAttributeContainer.clear()
                            ftpPropertyAttributeContainer.addAll(sPropertySubCatAdapter.getItem(position)!!.SubCatAttrContainers!!)
                            for (i in 0 until AttrabiuteProperty.length()){
                                val attribObj = AttrabiuteProperty.getJSONObject(i)
                                for (c in 0 until ftpPropertyAttributeContainer.size ){
                                    for (a in 0 until ftpPropertyAttributeContainer[c].ContainerAttributes!!.size ){
                                        if (attribObj.getInt("attribute_value_id") == ftpPropertyAttributeContainer[c].ContainerAttributes!![a].AttributeId){
                                            ftpPropertyAttributeContainer[c].ContainerAttributes!![a].IsSelected = true
                                            if (ftpPropertyAttributeContainer[c].ContainerAttributes!![a].AttributeType == "value"){
                                                ftpPropertyAttributeContainer[c].ContainerAttributes!![a].AttributeValue = attribObj.getString("value")
                                            }
                                        }
                                    }
                                }
                            }

                            propertyAttrContainersRecView.notifyDataSetChanged()
                        }
                    }

                    binding.PropertyServiceSpinner.setSelection(0)


                    propertyAttrContainersRecView.notifyDataSetChanged()

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(propselectedlat.toDouble(),propselectedlng.toDouble()), 13F))
                    marker = googleMap.addMarker(MarkerOptions().position(LatLng(propselectedlat.toDouble(),propselectedlng.toDouble())))

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
                    if (commonFuncs.IsInSP(this@EditChaletScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditChaletScreen, Constants.KeyUserToken)
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

    fun SetUpMapDialog() {
        mapDialog = Dialog(this)
        mapDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mapDialog?.setCancelable(false)
        mapDialog?.setContentView(R.layout.ftp_dialog_property_map)
        val SubmitLocation = mapDialog?.findViewById<TextView>(R.id.SubmitLocation)
        SubmitLocation!!.setOnClickListener {
            if (propselectedlat.isNullOrEmpty() || propselectedlng.isNullOrEmpty()){
                commonFuncs.showDefaultDialog(this,"الخطوة مهمة","يجب عليك الضغط على الخريطة وتحديد مكان العقار والتأكيد")
            }else{
                binding.PropertyShowMap.text = "لقد تم إختيار مكانك على الخريطة"
                binding.PropertyShowMap.backgroundTintList = ColorStateList.valueOf(resources.getColor(
                    R.color.ft_green,null))
                mapDialog!!.dismiss()
            }
        }
        val window: Window = mapDialog?.window!!
        window.setBackgroundDrawable(
            ColorDrawable(resources
                .getColor(R.color.tk_dialog_bg, null))
        )
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mapDialog != null){
            if (mapDialog!!.isShowing){
                mapDialog!!.dismiss()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0


        googleMap.setOnMapClickListener {
            propselectedlat = it.latitude.toString()
            propselectedlng = it.longitude.toString()

            if (marker == null){
                marker = googleMap.addMarker(MarkerOptions().position(LatLng(it.latitude,it.longitude)))
            }else{
                marker!!.remove()
                marker = googleMap.addMarker(MarkerOptions().position(LatLng(it.latitude,it.longitude)))
            }
        }



    }




}