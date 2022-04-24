package com.example.funnytimesvendorapp.MainMenuSection.UserSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.FTPTransaction
import com.example.funnytimesvendorapp.RecViews.TransactionsRecView
import com.example.funnytimesvendorapp.databinding.TScreenUserTransactionBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class UserTransactionScreen : AppCompatActivity() {


    lateinit var binding:TScreenUserTransactionBinding

    val commonFuncs = CommonFuncs()

    val ftpTransactions = ArrayList<FTPTransaction>()
    lateinit var transactionsRecView: TransactionsRecView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TScreenUserTransactionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener {
            finish()
        }

        transactionsRecView = TransactionsRecView(ftpTransactions,this)
        binding.TransactionsRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        binding.TransactionsRecycler.adapter = transactionsRecView

        my_Transaction_Request()

    }

    fun my_Transaction_Request(){
        var url = Constants.APIMain + "api/vendor-app/user/page/transaction"

        try {
            commonFuncs.showLoadingDialog(this)
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
                    ftpTransactions.clear()
                    ftpTransactions.addAll(gson.fromJson(data.toString(),Array<FTPTransaction>::class.java).toList())
                    transactionsRecView.notifyDataSetChanged()



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
                    if (commonFuncs.IsInSP(this@UserTransactionScreen, Constants.KeyUserToken)){
                        params["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@UserTransactionScreen, Constants.KeyUserToken)
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
}