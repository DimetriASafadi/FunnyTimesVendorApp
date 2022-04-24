package com.example.funnytimesvendorapp.MainMenuSection.UserSection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.CommonSection.Constants.APIMain
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyUserToken
import com.example.funnytimesvendorapp.MainMenu
import com.example.funnytimesvendorapp.Models.FTPTransaction
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.databinding.TScreenUserEditProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset

class UserEditProfileScreen : AppCompatActivity() {

    lateinit var binding:TScreenUserEditProfileBinding


    var temptoken = ""
    var name = ""
    var whatsapp = ""
    var instagram = ""
    var facebook = ""

    var chosenpic = 0
    lateinit var chosenpicuri: Uri

    val client = OkHttpClient()
    val commonFuncs = CommonFuncs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (commonFuncs.IsInSP(this, Constants.KeyAppLanguage)){
            commonFuncs.setLocale2(this,commonFuncs.GetFromSP(this, Constants.KeyAppLanguage)!!)
        }else{
            commonFuncs.setLocale2(this,"ar")
        }
        binding = TScreenUserEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener {
            finish()
        }

        val startForProfileImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val uri: Uri = data?.data!!
                    chosenpicuri = uri
                    Log.e("chosenpicuri",chosenpicuri.path.toString())
                    binding.ProImage.setImageURI(uri)
                    chosenpic = 1
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }

        binding.ProPickImage.setOnClickListener {
//            pickimage()
            ImagePicker.with(this)
                .compress(1024)
                .crop()//Final image size will be less than 1 MB(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        binding.ConfirmDetails.setOnClickListener {
            name = binding.ProName.text.toString()
            whatsapp = binding.ProWhatsApp.text.toString()
            instagram = binding.ProInstagram.text.toString()
            facebook = binding.ProFacebook.text.toString()
            if (name.isNullOrEmpty()){
                Toast.makeText(this, "يجب عليك إدخال اسمك", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Update_Vendor_Request()
        }



        my_Profile_Request()

    }

    fun my_Profile_Request(){
        var url = Constants.APIMain + "/api/vendor-app/user/"
        try {
            commonFuncs.showLoadingDialog(this)
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val status = jsonobj.getJSONObject("status")
                    val data = status.getJSONObject("message")
                    val vendor = data.getJSONObject("vendor")



                    var whatsapp = if (!vendor.isNull("whatsapp")) vendor.getString("whatsapp") else ""
                    var facebook = if (!vendor.isNull("facebook")) vendor.getString("facebook") else ""
                    var instagram = if (!vendor.isNull("instagram")) vendor.getString("instagram") else ""
                    var img = if (!vendor.isNull("img")) vendor.getString("img") else ""

                    Glide.with(this)
                        .load(APIMain+img)
                        .centerCrop()
                        .placeholder(R.drawable.ft_broken_image)
                        .into(binding.ProImage)

                    binding.ProName.setText(data.getString("name"))
                    binding.ProPhone.text = data.getString("phone")
                    binding.ProWhatsApp.setText(whatsapp)
                    binding.ProFacebook.setText(facebook)
                    binding.ProInstagram.setText(instagram)

                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"خطأ في الاتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"خطأ في الاتصال","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params = HashMap<String,String>()
                    if (commonFuncs.IsInSP(this@UserEditProfileScreen, Constants.KeyUserToken)){
                        params["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@UserEditProfileScreen, Constants.KeyUserToken)
                    }
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }

    fun Update_Vendor_Request(){
        try {
            commonFuncs.showLoadingDialog(this)
            val multipartBody = MultipartBody.Builder()
            multipartBody.setType(MultipartBody.FORM)
            multipartBody.addFormDataPart("name", name.toString())
            if (!whatsapp.isNullOrEmpty()){
                multipartBody.addFormDataPart("whatsapp", whatsapp)
            }
            if (!instagram.isNullOrEmpty()){
                multipartBody.addFormDataPart("instagram", instagram)
            }
            if (!facebook.isNullOrEmpty()){
                multipartBody.addFormDataPart("facebook", facebook)
            }
            if (chosenpic != 0){
                val file1 = File(chosenpicuri.path)
                val fileRequestBody = file1.asRequestBody("image/jpg".toMediaType())
                val imagename = System.currentTimeMillis().toString()
                multipartBody.addFormDataPart("img", imagename, fileRequestBody)
            }

            temptoken = commonFuncs.GetFromSP(this,KeyUserToken).toString()

            val requestBody: RequestBody = multipartBody.build()
            val request: okhttp3.Request = okhttp3.Request.Builder()
                .addHeader("Authorization", "Bearer $temptoken")
                .url(Constants.APIMain +"api/vendor-app/user/add/vendor")
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
                        commonFuncs.showDefaultDialog(this@UserEditProfileScreen,"فشل في العملية","حصل خطأ ما أثناء عملية الدفع , تأكد من اتصالك بالانترنت أو حاول مرة أخرى")
                    }
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    runOnUiThread {
                        Log.e("onResponse",response.message.toString())
                        commonFuncs.WriteOnSP(this@UserEditProfileScreen,
                            Constants.KeyUserToken,temptoken)
                        commonFuncs.hideLoadingDialog()
                        Toast.makeText(this@UserEditProfileScreen, "تم تحديث ملفك الشخصي", Toast.LENGTH_SHORT).show()

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





}