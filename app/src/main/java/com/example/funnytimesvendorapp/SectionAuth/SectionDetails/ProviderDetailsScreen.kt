package com.example.funnytimesvendorapp.SectionAuth.SectionDetails

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.CommonSection.Constants.APIMain
import com.example.funnytimesvendorapp.CommonSection.Constants.KeyUserToken
import com.example.funnytimesvendorapp.MainMenu
import com.example.funnytimesvendorapp.Models.FTPCategory
import com.example.funnytimesvendorapp.Models.FTPSubCategory
import com.example.funnytimesvendorapp.SpinnerAdapters.SSubCategoryAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenProviderDetailsBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import java.io.File


class ProviderDetailsScreen : AppCompatActivity() {


    lateinit var binding:FtpScreenProviderDetailsBinding


    var phonenum = ""
    var temptoken = ""
    lateinit var ftpCategory : FTPCategory
    var lat = ""
    var lng = ""
    var category_id = ""
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
        commonFuncs.setLocale2(this,"ar")
        if (commonFuncs.IsInSP(this, Constants.KeyAppLanguage)){
            commonFuncs.setLocale2(this,commonFuncs.GetFromSP(this, Constants.KeyAppLanguage)!!)
        }else{
            commonFuncs.setLocale2(this,"ar")
        }
        binding = FtpScreenProviderDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.backBtn.setOnClickListener {
            finish()
        }
        phonenum = intent.getStringExtra("phonenum").toString()
        temptoken = intent.getStringExtra("temptoken").toString()
        ftpCategory = intent.getSerializableExtra("ftpCategory") as FTPCategory
        lat = intent.getStringExtra("lat").toString()
        lng = intent.getStringExtra("lng").toString()

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
        val ftpsubCategorys = ArrayList<FTPSubCategory>()
        ftpsubCategorys.addAll(ftpCategory.CategorySubs!!)
        val sFilterCitiesAdapter = SSubCategoryAdapter(this,ftpsubCategorys)
        binding.ProSubCatSpinner.adapter = sFilterCitiesAdapter
        binding.ProSubCatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category_id = sFilterCitiesAdapter.getItem(position)!!.SubCatId.toString()
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
            if (chosenpic == 0){
                Toast.makeText(this, "يجب عليك اختيار صورة لملفك الشخصي", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Add_Vendor_Request()
        }


    }
    fun Add_Vendor_Request(){
        try {
            commonFuncs.showLoadingDialog(this)
            val multipartBody = MultipartBody.Builder()
            multipartBody.setType(MultipartBody.FORM)
            multipartBody.addFormDataPart("name", name.toString())
            multipartBody.addFormDataPart("category_id", category_id)
            multipartBody.addFormDataPart("phone", phonenum)
            if (!whatsapp.isNullOrEmpty()){
                multipartBody.addFormDataPart("whatsapp", whatsapp)
            }
            if (!instagram.isNullOrEmpty()){
                multipartBody.addFormDataPart("instagram", instagram)
            }
            if (!facebook.isNullOrEmpty()){
                multipartBody.addFormDataPart("facebook", facebook)
            }
            if (!lat.isNullOrEmpty()){
                multipartBody.addFormDataPart("lat", lat)
            }
            if (!lng.isNullOrEmpty()){
                multipartBody.addFormDataPart("lng", lng)
            }
            val file1 = File(chosenpicuri.path)
            val fileRequestBody = file1.asRequestBody("image/jpg".toMediaType())
            val imagename = System.currentTimeMillis().toString()
            multipartBody.addFormDataPart("img", imagename, fileRequestBody)
            val requestBody: RequestBody = multipartBody.build()
            val request: okhttp3.Request = okhttp3.Request.Builder()
                .addHeader("Authorization", "Bearer $temptoken")
                .url(APIMain +"api/vendor-app/user/add/vendor")
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
                        commonFuncs.showDefaultDialog(this@ProviderDetailsScreen,"فشل في العملية","حصل خطأ ما أثناء عملية الدفع , تأكد من اتصالك بالانترنت أو حاول مرة أخرى")
                    }
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    runOnUiThread {
                        Log.e("onResponse",response.message.toString())
                        commonFuncs.WriteOnSP(this@ProviderDetailsScreen,KeyUserToken,temptoken)
                        commonFuncs.hideLoadingDialog()
                        val intent = Intent(this@ProviderDetailsScreen,MainMenu::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
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

    fun pickimage() {
//        ImagePicker.with(this)
//            .crop()
//            .galleryOnly()//Crop image(Optional), Check Customization for more option
//            .compress(1024)			//Final image size will be less than 1 MB(Optional)
//            .start()

//        CropImage.activity()
//            .setGuidelines(CropImageView.Guidelines.ON)
//            .start(this);
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == RESULT_OK) {
//                val resultUri = result.uri
//                chosenpicuri = resultUri
//                binding.ProImage.setImageURI(resultUri)
//                chosenpic = 1
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                val error = result.error
//                Toast.makeText(this, error.message.toString(), Toast.LENGTH_SHORT).show()
//                Log.e("EmodoError",error.message.toString())
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        commonFuncs.setLocale2(this,"ar")
        if (commonFuncs.IsInSP(this, Constants.KeyAppLanguage)){
            commonFuncs.setLocale2(this,commonFuncs.GetFromSP(this, Constants.KeyAppLanguage)!!)
        }else{
            commonFuncs.setLocale2(this,"ar")
        }
    }


}