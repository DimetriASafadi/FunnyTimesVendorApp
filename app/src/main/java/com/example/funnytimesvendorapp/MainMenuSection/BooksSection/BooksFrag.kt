package com.example.funnytimesvendorapp.MainMenuSection.BooksSection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.funnytimesvendorapp.RecViews.MyBokRecView
import com.example.funnytimesvendorapp.RecViews.MyOrdRecView
import com.example.funnytimesvendorapp.databinding.FtpFragBooksBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class BooksFrag: Fragment() {
    private var _binding: FtpFragBooksBinding? = null
    private val binding get() = _binding!!

    val commonFuncs = CommonFuncs()

    var ftpMyBok = ArrayList<FTPMyBook>()
    lateinit var myBokRecView: MyBokRecView

    var fTPMyOrders = ArrayList<FTPMyOrder>()
    lateinit var myOrdRecView: MyOrdRecView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FtpFragBooksBinding.inflate(inflater, container, false)


        myBokRecView = MyBokRecView(ftpMyBok,requireContext())
        myOrdRecView = MyOrdRecView(fTPMyOrders,requireContext())

        binding.OrBoksReycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        binding.OrBoksReycler.adapter = myBokRecView

       // getMyOrders_Request()
        getMyBooks_Request()




        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun getMyBooks_Request(){
        var url = Constants.APIMain + "api/vendor-app/booking"
        try {
            commonFuncs.showLoadingDialog(requireActivity())
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
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

    fun getMyOrders_Request(){
        var url = Constants.APIMain + "api/vendor-app/order"
        try {
            commonFuncs.showLoadingDialog(requireActivity())
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
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
}