package com.example.funnytimesvendorapp.AddNewSection

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import kotlin.collections.ArrayList


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
    var proppriceType = "static"
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
            proppriceType = "static"
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
            proppriceType = "dynamic"
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


        binding.PropertyAdd.setOnClickListener {

            propname = binding.PropertyName.text.toString()
            propdescription = binding.PropertyDesc.text.toString()
            proppolicy = binding.PropertyPolicy.text.toString()
            propdistric = binding.PropertyDistric.text.toString()
            propmorningprice = binding.PropertyMorning.text.toString()
            propeveningprice = binding.PropertyEvening.text.toString()
            propprice = binding.PropertyPrice.text.toString()
            propdepositprice = binding.PropertyDeposit.text.toString()



            var propname = ""
            var propdescription = ""
            var proppolicy = ""
            var proptype = ""
            var propcity = ""
            var propdistric = ""
            var propselectedlat = ""
            var propselectedlng = ""
            var propbookType = 1
            var proppriceType = "static"
            var propprice = ""
            var propmorningprice = ""
            var propeveningprice = ""
            var propdepositprice = ""
            var propchosenimage = ""

            if (propname.isNullOrEmpty()){

            }
        }

    }




//    fun Add_Chalet_Request(){
//        try {
//            commonFuncs.showLoadingDialog(this)
//            val multipartBody = MultipartBody.Builder()
//            multipartBody.setType(MultipartBody.FORM)
//            multipartBody.addFormDataPart("name", name.toString())
//            multipartBody.addFormDataPart("category_id", category_id)
//            multipartBody.addFormDataPart("phone", phonenum)
//            if (!whatsapp.isNullOrEmpty()){
//                multipartBody.addFormDataPart("whatsapp", whatsapp)
//            }
//            if (!instagram.isNullOrEmpty()){
//                multipartBody.addFormDataPart("instagram", instagram)
//            }
//            if (!facebook.isNullOrEmpty()){
//                multipartBody.addFormDataPart("facebook", facebook)
//            }
//            if (!lat.isNullOrEmpty()){
//                multipartBody.addFormDataPart("lat", lat)
//            }
//            if (!lng.isNullOrEmpty()){
//                multipartBody.addFormDataPart("lng", lng)
//            }
//            val file1 = File(chosenpicuri.path)
//            val fileRequestBody = file1.asRequestBody("image/jpg".toMediaType())
//            val imagename = System.currentTimeMillis().toString()
//            multipartBody.addFormDataPart("img", imagename, fileRequestBody)
//            val requestBody: RequestBody = multipartBody.build()
//            val request: okhttp3.Request = okhttp3.Request.Builder()
//                .addHeader("Authorization", "Bearer ${commonFuncs.GetFromSP(this,KeyUserToken)}")
//                .url(Constants.APIMain +"api/vendor-app/property/store")
//                .post(requestBody)
//                .build()
//            client.newCall(request).enqueue(object : Callback {
//                @Throws(IOException::class)
//                override fun onFailure(call: Call, e: java.io.IOException) {
//                    runOnUiThread {
//                        Log.e("onFailure","onFailure")
//                        Log.e("onFailure",call.toString())
//                        Log.e("onFailure",e.message.toString())
//                        Log.e("onFailure",e.toString())
//                        commonFuncs.hideLoadingDialog()
//                        commonFuncs.showDefaultDialog(this@NewChaletScreen,"فشل في العملية","حصل خطأ ما أثناء عملية الدفع , تأكد من اتصالك بالانترنت أو حاول مرة أخرى")
//                    }
//                }
//                @Throws(IOException::class)
//                override fun onResponse(call: Call, response: okhttp3.Response) {
//                    runOnUiThread {
//                        Log.e("onResponse",response.message.toString())
//                        commonFuncs.hideLoadingDialog()
//                        finish()
//                    }
//                }
//            })
//        } catch (e: IOException) {
//            Log.e("TryCatchFinal",e.message.toString()+"A7a")
//            e.printStackTrace()
//            commonFuncs.hideLoadingDialog()
//            commonFuncs.showDefaultDialog(this,"خطأ في الاتصال","حصل خطأ ما")
//        }
//    }





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
                            propbookType = sPropertyBookSpinner.getItem(position)!!.BookId
                            if (propbookType == 3){
                                if (proppriceType == "dynamic"){
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
                                if (proppriceType == "dynamic"){
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
}