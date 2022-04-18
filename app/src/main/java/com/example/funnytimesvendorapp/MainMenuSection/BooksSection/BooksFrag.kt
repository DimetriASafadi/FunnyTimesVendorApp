package com.example.funnytimesvendorapp.MainMenuSection.BooksSection

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.FTPMyBook
import com.example.funnytimesvendorapp.Models.FTPMyOrder
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.MyBokRecView
import com.example.funnytimesvendorapp.RecViews.MyOrdRecView
import com.example.funnytimesvendorapp.databinding.FtpDialogOrbokFilterBinding
import com.example.funnytimesvendorapp.databinding.FtpFragBooksBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BooksFrag: Fragment() {
    private var _binding: FtpFragBooksBinding? = null
    private val binding get() = _binding!!

    val commonFuncs = CommonFuncs()

    var ftpMyBok = ArrayList<FTPMyBook>()
    lateinit var myBokRecView: MyBokRecView

    var fTPMyOrders = ArrayList<FTPMyOrder>()
    lateinit var myOrdRecView: MyOrdRecView

    var name = ""
    var status = ""
    var pricefrom = ""
    var priceto = ""

    lateinit var filterDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FtpFragBooksBinding.inflate(inflater, container, false)


        binding.OrBokAll.setOnClickListener {
            binding.OrBokAll.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_white,null))
            binding.OrBokAll.setTextColor(resources.getColor(R.color.ft_dark_blue,null))
            binding.OrBokPending.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_grey,null))
            binding.OrBokPending.setTextColor(resources.getColor(R.color.ft_menu_unselected,null))
            binding.OrBokCancel.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_grey,null))
            binding.OrBokCancel.setTextColor(resources.getColor(R.color.ft_menu_unselected,null))
            status = ""
            // getMyOrders_Request()
            getMyBooks_Request(false)

        }
        binding.OrBokPending.setOnClickListener {
            binding.OrBokAll.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_grey,null))
            binding.OrBokAll.setTextColor(resources.getColor(R.color.ft_menu_unselected,null))
            binding.OrBokPending.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_white,null))
            binding.OrBokPending.setTextColor(resources.getColor(R.color.ft_dark_blue,null))
            binding.OrBokCancel.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_grey,null))
            binding.OrBokCancel.setTextColor(resources.getColor(R.color.ft_menu_unselected,null))
            status = "unpaid"
            // getMyOrders_Request()
            getMyBooks_Request(false)
        }
        binding.OrBokCancel.setOnClickListener {
            binding.OrBokAll.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_grey,null))
            binding.OrBokAll.setTextColor(resources.getColor(R.color.ft_menu_unselected,null))
            binding.OrBokPending.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_grey,null))
            binding.OrBokPending.setTextColor(resources.getColor(R.color.ft_menu_unselected,null))
            binding.OrBokCancel.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ft_white,null))
            binding.OrBokCancel.setTextColor(resources.getColor(R.color.ft_dark_blue,null))
            status = "placed"
            // getMyOrders_Request()
            getMyBooks_Request(false)
        }


        myBokRecView = MyBokRecView(ftpMyBok,requireContext())
        myOrdRecView = MyOrdRecView(fTPMyOrders,requireContext())

        binding.OrBoksReycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.OrBoksReycler.adapter = myBokRecView

        binding.SearchBar.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // getMyOrders_Request()
                getMyBooks_Request(true)
                return@OnEditorActionListener true
            }
            false
        })

       // getMyOrders_Request()
        getMyBooks_Request(false)



        SetUpFilterDialog()
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        if (filterDialog != null){
            if (filterDialog.isShowing){
                filterDialog.dismiss()
            }
        }
    }



    fun getMyBooks_Request(isBar:Boolean){
        var url = Constants.APIMain + "api/vendor-app/booking?"
        if (isBar){
            name = binding.SearchBar.text.toString()
            url = url+"&name=$name"
        }else{
            if (!status.isNullOrEmpty()){
                url = url+"&status=$status"
            }
            if (!pricefrom.isNullOrEmpty()){
                url = url+"&price[from]=$pricefrom"
            }
            if (!priceto.isNullOrEmpty()){
                url = url+"&price[to]=$priceto"
            }
        }
        try {
            commonFuncs.showLoadingDialog(requireActivity())
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
                    ftpMyBok.clear()
                    ftpMyBok.addAll(gson.fromJson(data.toString(),Array<FTPMyBook>::class.java).toList())
                    myBokRecView.notifyDataSetChanged()
                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(requireContext(),"خطأ في الاتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(requireContext(),"خطأ في الاتصال","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params = HashMap<String,String>()
                    if (commonFuncs.IsInSP(requireContext(), Constants.KeyUserToken)){
                        params["Authorization"] = "Bearer "+commonFuncs.GetFromSP(requireContext(), Constants.KeyUserToken)
                    }
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(requireContext())
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }

    fun getMyOrders_Request(isBar:Boolean){
        var url = Constants.APIMain + "api/vendor-app/order?"
        if (isBar){
            name = binding.SearchBar.text.toString()
            url = url+"&name=$name"
        }else{
            if (!status.isNullOrEmpty()){
                url = url+"&status=$status"
            }
            if (!pricefrom.isNullOrEmpty()){
                url = url+"&price[from]=$pricefrom"
            }
            if (!priceto.isNullOrEmpty()){
                url = url+"&price[to]=$priceto"
            }
        }
        try {
            commonFuncs.showLoadingDialog(requireActivity())
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
                    fTPMyOrders.clear()
                    fTPMyOrders.addAll(gson.fromJson(data.toString(),Array<FTPMyOrder>::class.java).toList())
                    myOrdRecView.notifyDataSetChanged()
                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(requireContext(),"خطأ في الاتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(requireContext(),"خطأ في الاتصال","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params = HashMap<String,String>()
                    if (commonFuncs.IsInSP(requireContext(), Constants.KeyUserToken)){
                        params["Authorization"] = "Bearer "+commonFuncs.GetFromSP(requireContext(), Constants.KeyUserToken)
                    }
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(requireContext())
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }



    fun SetUpFilterDialog() {

        filterDialog = Dialog(requireContext())
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        filterDialog.setCancelable(true)
        val dialogBinding = FtpDialogOrbokFilterBinding.inflate(layoutInflater)
        filterDialog.setContentView(dialogBinding.root)

        dialogBinding.PriceRangeBar.setOnRangeChangedListener { view, min, max, isFromUser ->
            dialogBinding.BarMax.text = max.toInt().toString()+" ر.س"
            dialogBinding.BarMin.text = min.toInt().toString()+" ر.س"
            pricefrom = min.toInt().toString()
            priceto = max.toInt().toString()
        }



        dialogBinding.FilterResults!!.setOnClickListener {
            if (filterDialog!!.isShowing){
                filterDialog!!.dismiss()
            }
            getMyBooks_Request(false)
        }

        dialogBinding.FilterClear!!.setOnClickListener {
            dialogBinding.PriceRangeBar.setValue(0f,0f)
        }


        val window: Window = filterDialog.window!!
        window.setBackgroundDrawable(
            ColorDrawable(this.resources
                .getColor(R.color.tk_dialog_bg, null))
        )
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        binding.SearchFilter.setOnClickListener {
            filterDialog.show()
        }
    }

}