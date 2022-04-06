package com.example.funnytimesvendorapp.MyProductSection

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.FTPCategory
import com.example.funnytimesvendorapp.Models.FTPMyItem
import com.example.funnytimesvendorapp.Models.FTPSubCategory
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.MyItemsRecView
import com.example.funnytimesvendorapp.SpinnerAdapters.SSubCategoryAdapter
import com.example.funnytimesvendorapp.databinding.FtpDialogMyItemsFilterBinding
import com.example.funnytimesvendorapp.databinding.FtpScreenMyProductBinding
import com.google.gson.GsonBuilder
import com.jaygoo.widget.RangeSeekBar
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MyProductScreen : AppCompatActivity() {


    var PublishedDate: Calendar = Calendar.getInstance()

    lateinit var binding:FtpScreenMyProductBinding
    val commonFuncs = CommonFuncs()

    val ftpCategory = ArrayList<FTPCategory>()

    var filterDialog: Dialog? = null

    val ftpMyItem = ArrayList<FTPMyItem>()
    lateinit var myItemsRecView:MyItemsRecView



    var sub_category_id = ""
    var pricefrom = ""
    var name = ""
    var priceto = ""
    var publishDay = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenMyProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.backBtn.setOnClickListener {
            finish()
        }

        myItemsRecView = MyItemsRecView(ftpMyItem,this)
        binding.MyItemsRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,
            false)
        binding.MyItemsRecycler.adapter = myItemsRecView

        binding.SearchBar.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                FilterSearch_Request(true)
                return@OnEditorActionListener true
            }
            false
        })



        Category_Request()


    }



    fun get_myProducts_Request(){
        val url = Constants.APIMain + "api/vendor-app/product/get"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
                    ftpMyItem.clear()
                    ftpMyItem.addAll(gson.fromJson(data.toString(),Array<FTPMyItem>::class.java).toList())
                    myItemsRecView.notifyDataSetChanged()

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
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String,String>()
                    if (commonFuncs.IsInSP(this@MyProductScreen, Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@MyProductScreen, Constants.KeyUserToken)
                    }
                    return map
                }
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
        }
    }





    fun Category_Request(){
        var url = Constants.APIMain + "api/vendor-app/categories"
        try {
            commonFuncs.showLoadingDialog(this)
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
                    ftpCategory.addAll(gson.fromJson(data.toString(),Array<FTPCategory>::class.java).toList())
                    SetUpFilterDialog()
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
                    if (commonFuncs.IsInSP(this@MyProductScreen, Constants.KeyUserToken)){
                        params["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@MyProductScreen, Constants.KeyUserToken)
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

    fun SetUpFilterDialog() {

        filterDialog = Dialog(this)
        filterDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        filterDialog?.setCancelable(true)
        val dialogBinding = FtpDialogMyItemsFilterBinding.inflate(layoutInflater)
        filterDialog?.setContentView(dialogBinding.root)
        val filterSubCategory = ArrayList<FTPSubCategory>()
        filterSubCategory.addAll(ftpCategory[0].CategorySubs!!)
        val sFilterSubCategoryAdapter = SSubCategoryAdapter(this,filterSubCategory)
        dialogBinding.FilterTypeSpinner.adapter = sFilterSubCategoryAdapter

        dialogBinding.PriceRangeBar.setOnRangeChangedListener { view, min, max, isFromUser ->
            dialogBinding.BarMax.text = max.toInt().toString()+" ر.س"
            dialogBinding.BarMin.text = min.toInt().toString()+" ر.س"
            pricefrom = min.toInt().toString()
            priceto = max.toInt().toString()
        }


        dialogBinding.FilterTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sub_category_id = sFilterSubCategoryAdapter.getItem(position)!!.SubCatId.toString()
            }
        }
        dialogBinding.FilterResults!!.setOnClickListener {
            if (filterDialog!!.isShowing){
                filterDialog!!.dismiss()
            }
            FilterSearch_Request(false)
        }

        dialogBinding.FilterClear!!.setOnClickListener {
            dialogBinding.FilterTypeSpinner.setSelection(0)
            dialogBinding.PriceRangeBar.setValue(0f,0f)
        }

        val year = PublishedDate.get(Calendar.YEAR)
        val month = PublishedDate.get(Calendar.MONTH)
        val day = PublishedDate.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            dialogBinding.FilterDateText.text = "$year-$monthOfYear-$dayOfMonth"
            publishDay = "$year-$monthOfYear-$dayOfMonth"

        }, year, month, day)

        dialogBinding.FilterPublishDate.setOnClickListener {
            dpd.show()
        }
        val window: Window = filterDialog?.window!!
        window.setBackgroundDrawable(
            ColorDrawable(this.resources
                .getColor(R.color.tk_dialog_bg, null))
        )
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        binding.SearchFilter.setOnClickListener {
            filterDialog!!.show()
        }
    }


    fun FilterSearch_Request(isBar:Boolean){


        var url = Constants.APIMain + "api/vendor-app/filter/?"
        if (isBar){
            name = binding.SearchBar.text.toString()
            url = url+"&name=$name"
        }else{

//            if (!name.isNullOrEmpty()){
//                url = url+"&name=$name"
//            }
            if (pricefrom != "0" && priceto != "0"){
                if (!pricefrom.isNullOrEmpty()){
                    url = url+"&price[from]=$pricefrom"
                }
                if (!priceto.isNullOrEmpty()){
                    url = url+"&price[to]=$priceto"
                }
            }

            if (!sub_category_id.isNullOrEmpty()){
                url = url+"&sub_category_id=$sub_category_id"
            }
            if (!publishDay.isNullOrEmpty()){
                url = url+"&created_at=$publishDay"
            }
        }
        try {
            commonFuncs.showLoadingDialog(this)
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val products = data.getJSONArray("products")
                    val gson = GsonBuilder().create()
                    ftpMyItem.clear()
                    ftpMyItem.addAll(gson.fromJson(products.toString(),Array<FTPMyItem>::class.java).toList())
                    myItemsRecView.notifyDataSetChanged()
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
                    if (commonFuncs.IsInSP(this@MyProductScreen, Constants.KeyUserToken)){
                        params["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@MyProductScreen, Constants.KeyUserToken)
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


    override fun onDestroy() {
        super.onDestroy()
        if (filterDialog != null){
            if (filterDialog!!.isShowing){
                filterDialog!!.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        get_myProducts_Request()


    }

}