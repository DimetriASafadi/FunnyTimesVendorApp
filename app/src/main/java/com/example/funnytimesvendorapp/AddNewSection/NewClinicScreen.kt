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
import com.example.funnytimesvendorapp.Models.FTPPropPhoto
import com.example.funnytimesvendorapp.RecViews.PropertyPhotoRecView
import com.example.funnytimesvendorapp.SpinnerAdapters.SClinicTypeAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenNewClinicBinding
import com.google.gson.GsonBuilder
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.HashMap

class NewClinicScreen : AppCompatActivity() {

    val ftpClinicType = ArrayList<FTPClinicType>()
    val ftpPropPhotos = ArrayList<FTPPropPhoto>()
    val commonFuncs = CommonFuncs()

    var clinicname = ""
    var clinictype = ""
    var clinicdescription = ""
    var clinicdidservices = ""
    var clinicdidimage = ""

    lateinit var binding:FtpScreenNewClinicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenNewClinicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)





        val propertyPhotoRecView = PropertyPhotoRecView(ftpPropPhotos,this)
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
            if (clinicdidimage.isNullOrEmpty()){
                Toast.makeText(this, "يجب عليك اختيار صورة واحدة على الأقل لعقارك", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }



        }
        Clinic_tools_Request()
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
                    if (commonFuncs.IsInSP(this@NewClinicScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@NewClinicScreen, Constants.KeyUserToken)
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