package com.example.funnytimesvendorapp.EditsSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.FTPItemPhoto
import com.example.funnytimesvendorapp.Models.FTPPropertyBook
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.SpinnerAdapters.SPropertyBookSpinner
import com.example.funnytimesvendorapp.databinding.FtpScreenEditChaletPriceBinding
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.HashMap

class EditChaletPriceScreen : AppCompatActivity() {


    val commonFuncs = CommonFuncs()
    val ftpPropertyBook = ArrayList<FTPPropertyBook>()

    var propid = ""
    var propbookType = 1
    var proppriceType = "fix"
    var propprice = ""
    var propmorningprice = ""
    var propeveningprice = ""
    var propdepositprice = ""

    var prices1 = ArrayList<EditText>()
    var prices2 = ArrayList<EditText>()

    val client = OkHttpClient()


    lateinit var binding:FtpScreenEditChaletPriceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FtpScreenEditChaletPriceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        propid = intent.getStringExtra("ItemId").toString()

        binding.backBtn.setOnClickListener {
            finish()
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

        GetSetCurrentData()

        binding.EditPrice.setOnClickListener {


            propmorningprice = binding.PropertyMorning.text.toString()
            propeveningprice = binding.PropertyEvening.text.toString()
            propprice = binding.PropertyPrice.text.toString()
            propdepositprice = binding.PropertyDeposit.text.toString()

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

            SetNewPrice_Request()

        }

    }


    fun GetSetCurrentData() {
        Log.e("GetSetCurrentData", "GetSetCurrentData")
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/property/$propid"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    proppriceType = data.getString("priceType").toString()

                    if (proppriceType == "fix"){
                        binding.StaticButton.setBackgroundResource(R.drawable.ft_radius_fill)
                        binding.StaticText.setTextColor(resources.getColor(R.color.ft_dark_blue, null))
                        binding.DynamicButton.setBackgroundResource(0)
                        binding.DynamicText.setTextColor(resources.getColor(R.color.ft_grey_1, null))
                    }else{
                        binding.DynamicButton.setBackgroundResource(R.drawable.ft_radius_fill)
                        binding.DynamicText.setTextColor(resources.getColor(R.color.ft_dark_blue, null))
                        binding.StaticButton.setBackgroundResource(0)
                        binding.StaticText.setTextColor(resources.getColor(R.color.ft_grey_1, null))
                    }

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
                            if (propbookType == 3) {
                                if (proppriceType == "change") {
                                    binding.PropertyEveningSection.visibility = View.VISIBLE
                                    binding.PropertyMainPriceTV.text = "الفترة الصباحية"
                                    binding.PropertyDynamicPriceSection.visibility = View.VISIBLE
                                    binding.PropertyNormalPriceSection.visibility = View.GONE
                                    binding.PropertyPeriodPriceSection.visibility = View.GONE
                                } else {
                                    binding.PropertyDynamicPriceSection.visibility = View.GONE
                                    binding.PropertyNormalPriceSection.visibility = View.GONE
                                    binding.PropertyPeriodPriceSection.visibility = View.VISIBLE
                                }
                            } else {
                                if (proppriceType == "change") {
                                    binding.PropertyEveningSection.visibility = View.GONE
                                    binding.PropertyMainPriceTV.text = "السعر"
                                    binding.PropertyDynamicPriceSection.visibility = View.VISIBLE
                                    binding.PropertyNormalPriceSection.visibility = View.GONE
                                    binding.PropertyPeriodPriceSection.visibility = View.GONE
                                } else {
                                    binding.PropertyDynamicPriceSection.visibility = View.GONE
                                    binding.PropertyNormalPriceSection.visibility = View.VISIBLE
                                    binding.PropertyPeriodPriceSection.visibility = View.GONE
                                }
                            }
                        }
                    }
                    binding.PropertyBookingSpinner.setSelection(data.getInt("bookingType")-1)

                    val priceTable = data.getJSONObject("priceTable")
                    if (data.getInt("bookingType") == 3){
                        if (proppriceType == "fix"){
                            binding.PropertyMorning.setText(priceTable.getInt("priceOnDay").toString())
                            binding.PropertyEvening.setText(priceTable.getInt("priceOnNight").toString())
                            binding.PropertyDeposit.setText(data.getString("deposit").toString())
                        }else{
                            binding.PriceSaturday.setText(priceTable.getJSONObject("Sat").getInt("day"))
                            binding.PriceSunday.setText(priceTable.getJSONObject("Sun").getInt("day"))
                            binding.PriceMonday.setText(priceTable.getJSONObject("Mon").getInt("day"))
                            binding.PriceTuseday.setText(priceTable.getJSONObject("Tue").getInt("day"))
                            binding.PriceWednsday.setText(priceTable.getJSONObject("Wed").getInt("day"))
                            binding.PriceThursday.setText(priceTable.getJSONObject("Thu").getInt("day"))
                            binding.PriceFriday.setText(priceTable.getJSONObject("Fri").getInt("day"))
                            binding.EPriceSaturday.setText(priceTable.getJSONObject("Sat").getInt("night"))
                            binding.EPriceSunday.setText(priceTable.getJSONObject("Sun").getInt("night"))
                            binding.EPriceMonday.setText(priceTable.getJSONObject("Mon").getInt("night"))
                            binding.EPriceTuseday.setText(priceTable.getJSONObject("Tue").getInt("night"))
                            binding.EPriceWednsday.setText(priceTable.getJSONObject("Wed").getInt("night"))
                            binding.EPriceThursday.setText(priceTable.getJSONObject("Thu").getInt("night"))
                            binding.EPriceFriday.setText(priceTable.getJSONObject("Fri").getInt("night"))

                            binding.PropertyDeposit.setText(data.getString("deposit").toString())

                        }
                    }else{
                        if (proppriceType == "fix"){
                            binding.PropertyPrice.setText(priceTable.getInt("price"))
                            binding.PropertyDeposit.setText(data.getString("deposit").toString())
                        }else{
                            binding.PriceSaturday.setText(priceTable.getInt("Sat").toString())
                            binding.PriceSunday.setText(priceTable.getInt("Sun").toString())
                            binding.PriceMonday.setText(priceTable.getInt("Mon").toString())
                            binding.PriceTuseday.setText(priceTable.getInt("Tue").toString())
                            binding.PriceWednsday.setText(priceTable.getInt("Wed").toString())
                            binding.PriceThursday.setText(priceTable.getInt("Thu").toString())
                            binding.PriceFriday.setText(priceTable.getInt("Fri").toString())

                            binding.PropertyDeposit.setText(data.getString("deposit").toString())

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
                    if (commonFuncs.IsInSP(this@EditChaletPriceScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditChaletPriceScreen, Constants.KeyUserToken)
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

    fun SetNewPrice_Request() {
        Log.e("GetSetCurrentData", "GetSetCurrentData")
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/property/price/$propid"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.POST, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())

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
                    if (commonFuncs.IsInSP(this@EditChaletPriceScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@EditChaletPriceScreen, Constants.KeyUserToken)
                    }
                    return map
                }

                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String,String>()
                    params["type"] = propbookType.toString()
                    params["priceType"] = proppriceType
                    if (propbookType == 3){
                        if (proppriceType == "change"){
                            params["Sat2"] = binding.PriceSaturday.text.toString()
                            params["Sun2"] = binding.PriceSunday.text.toString()
                            params["Mon2"] = binding.PriceMonday.text.toString()
                            params["Tues2"] = binding.PriceTuseday.text.toString()
                            params["Wed2"] =  binding.PriceWednsday.text.toString()
                            params["Thurs2"] = binding.PriceThursday.text.toString()
                            params["Fri2"] = binding.PriceFriday.text.toString()
                            params["Sat3"] = binding.EPriceSaturday.text.toString()
                            params["Sun3"] = binding.EPriceSunday.text.toString()
                            params["Mon3"] = binding.EPriceMonday.text.toString()
                            params["Tues3"] = binding.EPriceTuseday.text.toString()
                            params["Wed3"] = binding.EPriceWednsday.text.toString()
                            params["Thurs3"] = binding.EPriceThursday.text.toString()
                            params["Fri3"] = binding.EPriceFriday.text.toString()
                        }else{
                            params["priceOnDay"] =  propmorningprice
                            params["priceOnNight"] =  propeveningprice
                        }
                    }else{
                        if (proppriceType == "change"){
                            params["Sat"] = binding.PriceSaturday.text.toString()
                            params["Sun"] =  binding.PriceSunday.text.toString()
                            params["Mon"] =  binding.PriceMonday.text.toString()
                            params["Tue"] =  binding.PriceTuseday.text.toString()
                            params["Wed"] =  binding.PriceWednsday.text.toString()
                            params["Thu"] =  binding.PriceThursday.text.toString()
                            params["Fri"] =  binding.PriceFriday.text.toString()
                        }else{
                            params["price"] =  propprice

                        }
                    }

                    params["deposit"] = propdepositprice

                    Log.e("params",params.toString())

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


}