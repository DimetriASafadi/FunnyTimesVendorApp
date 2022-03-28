package com.example.funnytimesvendorapp.AddNewSection

import android.app.Dialog
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyUserToken
import com.example.funnytimesvendorapp.Models.*
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.PropertyAttrContainersRecView
import com.example.funnytimesvendorapp.RecViews.PropertyPhotoRecView
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertyBookSpinner
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertyCityAdapter
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertySubCatAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenNewChaletBinding
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
import java.io.File
import java.nio.charset.Charset
import java.util.*


class NewChaletScreen : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding:FtpScreenNewChaletBinding
    val commonFuncs = CommonFuncs()

    val ftpPropertyBook = ArrayList<FTPPropertyBook>()
    val ftpPropertySubCat = ArrayList<FTPPropertySubCat>()
    val ftpPropertyCity = ArrayList<FTPPropertyCity>()
    val ftpPropPhotos = ArrayList<FTPPropPhoto>()

    var propname = ""
    var propdescription = ""
    var proppolicy = ""
    var proptype = ""
    var propcity = ""
    var propdistric = ""
    var propselectedlat = ""
    var propselectedlng = ""
    var propbookType = 1
    var proppriceType = "fix"
    var propprice = ""
    var propmorningprice = ""
    var propeveningprice = ""
    var propdepositprice = ""
    var propchosenimage = ""

    var prices1 = ArrayList<EditText>()
    var prices2 = ArrayList<EditText>()

    var mapDialog: Dialog? = null
    lateinit var googleMap: GoogleMap
    var marker:Marker? = null

    val client = OkHttpClient()

    lateinit var propertyAttrContainersRecView:PropertyAttrContainersRecView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenNewChaletBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener {
            finish()
        }


        SetUpMapDialog()
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.PropMapFrag) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        binding.PropertyShowMap.setOnClickListener {
            mapDialog?.show()
        }

        prices1.addAll(listOf(binding.PriceSaturday,binding.PriceSunday,binding.PriceMonday,binding.PriceTuseday,binding.PriceWednsday,binding.PriceThursday,binding.PriceFriday))
        prices2.addAll(listOf(binding.EPriceSaturday,binding.EPriceSunday,binding.EPriceMonday,binding.EPriceTuseday,binding.EPriceWednsday,binding.EPriceThursday,binding.EPriceFriday))

        binding.StaticButton.setOnClickListener {
            binding.StaticButton.setBackgroundResource(R.drawable.ft_radius_fill)
            binding.StaticText.setTextColor(resources.getColor(R.color.ft_dark_blue, null))
            binding.DynamicButton.setBackgroundResource(0)
            binding.DynamicText.setTextColor(resources.getColor(R.color.ft_grey_1, null))
            proppriceType = "fix"
            if (propbookType == 3) {
                binding.PropertyEveningSection.visibility = View.VISIBLE
                binding.PropertyMainPriceTV.text = "الفترة الصباحية"
                binding.PropertyDynamicPriceSection.visibility = View.GONE
                binding.PropertyNormalPriceSection.visibility = View.GONE
                binding.PropertyPeriodPriceSection.visibility = View.VISIBLE
            } else {
                binding.PropertyEveningSection.visibility = View.GONE
                binding.PropertyMainPriceTV.text = "السعر"
                binding.PropertyDynamicPriceSection.visibility = View.GONE
                binding.PropertyNormalPriceSection.visibility = View.VISIBLE
                binding.PropertyPeriodPriceSection.visibility = View.GONE
            }

        }
        binding.DynamicButton.setOnClickListener {
            binding.DynamicButton.setBackgroundResource(R.drawable.ft_radius_fill)
            binding.DynamicText.setTextColor(resources.getColor(R.color.ft_dark_blue, null))
            binding.StaticButton.setBackgroundResource(0)
            binding.StaticText.setTextColor(resources.getColor(R.color.ft_grey_1, null))
            proppriceType = "change"
            if (propbookType == 3) {
                binding.PropertyEveningSection.visibility = View.VISIBLE
                binding.PropertyMainPriceTV.text = "الفترة الصباحية"
                binding.PropertyDynamicPriceSection.visibility = View.VISIBLE
                binding.PropertyNormalPriceSection.visibility = View.GONE
                binding.PropertyPeriodPriceSection.visibility = View.GONE
            } else {
                binding.PropertyEveningSection.visibility = View.GONE
                binding.PropertyMainPriceTV.text = "السعر"
                binding.PropertyDynamicPriceSection.visibility = View.VISIBLE
                binding.PropertyNormalPriceSection.visibility = View.GONE
                binding.PropertyPeriodPriceSection.visibility = View.GONE
            }
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
                propchosenimage = "selected"
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


        binding.PropertyAdd.setOnClickListener {

            propname = binding.PropertyName.text.toString()
            propdescription = binding.PropertyDesc.text.toString()
            proppolicy = binding.PropertyPolicy.text.toString()
            propdistric = binding.PropertyDistric.text.toString()
            propmorningprice = binding.PropertyMorning.text.toString()
            propeveningprice = binding.PropertyEvening.text.toString()
            propprice = binding.PropertyPrice.text.toString()
            propdepositprice = binding.PropertyDeposit.text.toString()



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
            if (propselectedlat.isNullOrEmpty() || propselectedlng.isNullOrEmpty()){
                Toast.makeText(this, "يجب عليك إختيار موقعك على الخريطة", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (propbookType == 3){
                if (proppriceType == "change"){
                    for (edittext in prices1){
                        if (edittext.text.isNullOrEmpty()){
                            edittext.error = "لا يمكن ترك الحقل فارغ"
                            edittext.requestFocus()
                            return@setOnClickListener
                        }
                    }
                    for (edittext in prices2){
                        if (edittext.text.isNullOrEmpty()){
                            edittext.error = "لا يمكن ترك الحقل فارغ"
                            edittext.requestFocus()
                            return@setOnClickListener
                        }
                    }
                }else{
                    if (propmorningprice.isNullOrEmpty()){
                        binding.PropertyMorning.error = "لا يمكن ترك الحقل فارغ"
                        binding.PropertyMorning.requestFocus()
                        return@setOnClickListener
                    }
                    if (propeveningprice.isNullOrEmpty()){
                        binding.PropertyEvening.error = "لا يمكن ترك الحقل فارغ"
                        binding.PropertyEvening.requestFocus()
                        return@setOnClickListener
                    }
                }
            }else{
                if (proppriceType == "change"){
                    for (edittext in prices1){
                        if (edittext.text.isNullOrEmpty()){
                            edittext.error = "لا يمكن ترك الحقل فارغ"
                            edittext.requestFocus()
                            return@setOnClickListener
                        }
                    }
                }else{
                    if (propprice.isNullOrEmpty()){
                        binding.PropertyPrice.error = "لا يمكن ترك الحقل فارغ"
                        binding.PropertyPrice.requestFocus()
                        return@setOnClickListener
                    }
                }
            }

            if (propdepositprice.isNullOrEmpty()){
                binding.PropertyDeposit.error = "لا يمكن ترك الحقل فارغ"
                binding.PropertyDeposit.requestFocus()
                return@setOnClickListener
            }


            if (propchosenimage.isNullOrEmpty()){
                Toast.makeText(this, "يجب عليك اختيار صورة واحدة على الأقل لعقارك", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Add_Chalet_Request()


        }

    }




    fun Add_Chalet_Request(){
        try {
            commonFuncs.showLoadingDialog(this)
            val multipartBody = MultipartBody.Builder()
            multipartBody.setType(MultipartBody.FORM)
            multipartBody.addFormDataPart("name", propname)
            multipartBody.addFormDataPart("description", propdepositprice)
            multipartBody.addFormDataPart("policy", proppolicy)
            multipartBody.addFormDataPart("sub_category_id", proptype)
            multipartBody.addFormDataPart("city_id", propcity)
            multipartBody.addFormDataPart("address", propdistric)
            multipartBody.addFormDataPart("type", propbookType.toString())
            multipartBody.addFormDataPart("priceType", proppriceType)

            if (propbookType == 3){
                if (proppriceType == "change"){
                    multipartBody.addFormDataPart("Sat2", binding.PriceSaturday.text.toString())
                    multipartBody.addFormDataPart("Sun2", binding.PriceSunday.text.toString())
                    multipartBody.addFormDataPart("Mon2", binding.PriceMonday.text.toString())
                    multipartBody.addFormDataPart("Tue2", binding.PriceTuseday.text.toString())
                    multipartBody.addFormDataPart("Wed2", binding.PriceWednsday.text.toString())
                    multipartBody.addFormDataPart("Thu2", binding.PriceThursday.text.toString())
                    multipartBody.addFormDataPart("Fri2", binding.PriceFriday.text.toString())
                    multipartBody.addFormDataPart("Sat3", binding.EPriceSaturday.text.toString())
                    multipartBody.addFormDataPart("Sun3", binding.EPriceSunday.text.toString())
                    multipartBody.addFormDataPart("Mon3", binding.EPriceMonday.text.toString())
                    multipartBody.addFormDataPart("Tue3", binding.EPriceTuseday.text.toString())
                    multipartBody.addFormDataPart("Wed3", binding.EPriceWednsday.text.toString())
                    multipartBody.addFormDataPart("Thu3", binding.EPriceThursday.text.toString())
                    multipartBody.addFormDataPart("Fri3", binding.EPriceFriday.text.toString())
                }else{
                    multipartBody.addFormDataPart("priceOnDay", propmorningprice)
                    multipartBody.addFormDataPart("priceOnNight", propeveningprice)
                }
            }else{
                if (proppriceType == "change"){
                    multipartBody.addFormDataPart("Sat", binding.PriceSaturday.text.toString())
                    multipartBody.addFormDataPart("Sun", binding.PriceSunday.text.toString())
                    multipartBody.addFormDataPart("Mon", binding.PriceMonday.text.toString())
                    multipartBody.addFormDataPart("Tue", binding.PriceTuseday.text.toString())
                    multipartBody.addFormDataPart("Wed", binding.PriceWednsday.text.toString())
                    multipartBody.addFormDataPart("Thu", binding.PriceThursday.text.toString())
                    multipartBody.addFormDataPart("Fri", binding.PriceFriday.text.toString())
                }else{
                    multipartBody.addFormDataPart("price", propprice)

                }
            }
            multipartBody.addFormDataPart("deposit", propdepositprice)
            multipartBody.addFormDataPart("lat", propselectedlat)
            multipartBody.addFormDataPart("lng", propselectedlng)

            for(photo in ftpPropPhotos){
                val file1 = commonFuncs.getFileFromUri(photo.PhotoUri!!,this)
//                val file1 = File(getRealPath(photo.PhotoUri!!))
                val fileRequestBody = file1!!.asRequestBody("image/jpg".toMediaType())
                val imagename = System.currentTimeMillis().toString()
                multipartBody.addFormDataPart("imgs[]", imagename, fileRequestBody)
            }

            for (container in propertyAttrContainersRecView.GetAttributesContainer()){
                for (attribute in container.ContainerAttributes!!){
                    if (attribute.AttributeType == "value"){
                        multipartBody.addFormDataPart("attr[${container.ContainerId}][${attribute.AttributeId}]", attribute.AttributeValue.toString())
                    }else{
                        multipartBody.addFormDataPart("attr[${container.ContainerId}][${attribute.AttributeId}]", "on")
                    }

                }
            }

            val requestBody: RequestBody = multipartBody.build()
            val request: okhttp3.Request = okhttp3.Request.Builder()
                .addHeader("Authorization", "Bearer ${commonFuncs.GetFromSP(this,KeyUserToken)}")
                .url(Constants.APIMain +"api/vendor-app/property/store")
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
                        commonFuncs.showDefaultDialog(this@NewChaletScreen,"فشل في العملية","حصل خطأ ما أثناء عملية الدفع , تأكد من اتصالك بالانترنت أو حاول مرة أخرى")
                    }
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    runOnUiThread {
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
                binding.PropertyShowMap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_green,null))
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
                    propertyAttrContainersRecView = PropertyAttrContainersRecView(ftpPropertyAttributeContainer,this)
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
                            propbookType = sPropertyBookSpinner.getItem(position)!!.BookId
                            if (propbookType == 3){
                                if (proppriceType == "change"){
                                    binding.PropertyEveningSection.visibility = View.VISIBLE
                                    binding.PropertyMainPriceTV.text = "الفترة الصباحية"
                                    binding.PropertyDynamicPriceSection.visibility = View.VISIBLE
                                    binding.PropertyNormalPriceSection.visibility = View.GONE
                                    binding.PropertyPeriodPriceSection.visibility = View.GONE
                                }else{
                                    binding.PropertyDynamicPriceSection.visibility = View.GONE
                                    binding.PropertyNormalPriceSection.visibility = View.GONE
                                    binding.PropertyPeriodPriceSection.visibility = View.VISIBLE
                                }
                            }else{
                                if (proppriceType == "change"){
                                    binding.PropertyEveningSection.visibility = View.GONE
                                    binding.PropertyMainPriceTV.text = "السعر"
                                    binding.PropertyDynamicPriceSection.visibility = View.VISIBLE
                                    binding.PropertyNormalPriceSection.visibility = View.GONE
                                    binding.PropertyPeriodPriceSection.visibility = View.GONE
                                }else{
                                    binding.PropertyDynamicPriceSection.visibility = View.GONE
                                    binding.PropertyNormalPriceSection.visibility = View.VISIBLE
                                    binding.PropertyPeriodPriceSection.visibility = View.GONE
                                }
                            }
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
                            proptype = sPropertySubCatAdapter.getItem(position)!!.SubCatId.toString()
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
                            propcity = sPropertyCityAdapter.getItem(position)!!.CityId.toString()
                        }
                    }
                    propertyAttrContainersRecView.notifyDataSetChanged()
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
                    if (commonFuncs.IsInSP(this@NewChaletScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@NewChaletScreen, Constants.KeyUserToken)
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


//
//    fun getRealPath(uri:Uri):String{
//        var realPath = ""
//        val wholeID = DocumentsContract.getDocumentId(uri)
//        // Split at colon, use second item in the array
//        // Split at colon, use second item in the array
//        val id = wholeID.split(":").toTypedArray()[1]
//        val column = arrayOf(MediaStore.Images.Media.DATA)
//        // where id is equal to
//        // where id is equal to
//        val sel = MediaStore.Images.Media._ID + "=?"
//        val cursor: Cursor? = contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            column,
//            sel,
//            arrayOf(id),
//            null
//        )
//        var columnIndex = 0
//        if (cursor != null) {
//            columnIndex = cursor.getColumnIndex(column[0])
//            if (cursor.moveToFirst()) {
//                realPath = cursor.getString(columnIndex)
//            }
//            cursor.close()
//        }
//
//        return realPath;
//    }
}