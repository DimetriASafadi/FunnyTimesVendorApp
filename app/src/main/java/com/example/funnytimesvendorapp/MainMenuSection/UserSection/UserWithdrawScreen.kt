package com.example.funnytimesvendorapp.MainMenuSection.UserSection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.Models.FTPWithdrawMethod
import com.example.funnytimesvendorapp.SpinnerAdapters.SWithdrawMethodAdapter
import com.example.funnytimesvendorapp.databinding.FtpScreenUserWithdrawBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.HashMap

class UserWithdrawScreen : AppCompatActivity() {

    lateinit var binding:FtpScreenUserWithdrawBinding

    val ftpWithdrawMethods = ArrayList<FTPWithdrawMethod>()
    var type = ""
    var paypal_email = ""
    var bank_name = ""
    var IBAN = ""
    var amount = ""

    val commonFuncs = CommonFuncs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FtpScreenUserWithdrawBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener {
            finish()
        }

        ftpWithdrawMethods.add(FTPWithdrawMethod("bank","حوالة بنكية"))
        ftpWithdrawMethods.add(FTPWithdrawMethod("paypal","باي بال"))

        val withdrawMethodAdapter = SWithdrawMethodAdapter(this,ftpWithdrawMethods)
        binding.WithdrawMethodSpinner.adapter = withdrawMethodAdapter

        binding.WithdrawMethodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                type = withdrawMethodAdapter.getItem(position)!!.MethodId.toString()
                if (type == "bank"){
                    binding.BankSection.visibility = View.VISIBLE
                    binding.PaypalSection.visibility = View.GONE
                }else if(type == "paypal"){
                    binding.BankSection.visibility = View.GONE
                    binding.PaypalSection.visibility = View.VISIBLE
                }
            }
        }

        binding.WithDrawBtn.setOnClickListener {

            paypal_email = binding.PaypalEmail.text.toString()
            bank_name = binding.BankName.text.toString()
            IBAN = binding.IBAN.text.toString()
            amount = binding.MoneyAmount.text.toString()
            if (amount.isNullOrEmpty()){
                binding.MoneyAmount.error = "لا يمكن ترك الحقل فارغ"
                binding.MoneyAmount.requestFocus()
                return@setOnClickListener
            }

            if (type == "bank"){
                if (bank_name.isNullOrEmpty()){
                    binding.BankName.error = "لا يمكن ترك الحقل فارغ"
                    binding.BankName.requestFocus()
                    return@setOnClickListener
                }
                if (IBAN.isNullOrEmpty()){
                    binding.IBAN.error = "لا يمكن ترك الحقل فارغ"
                    binding.IBAN.requestFocus()
                    return@setOnClickListener
                }
            }else if (type == "paypal"){
                if (paypal_email.isNullOrEmpty()){
                    binding.PaypalEmail.error = "لا يمكن ترك الحقل فارغ"
                    binding.PaypalEmail.requestFocus()
                    return@setOnClickListener
                }
            }

            withDraw_Request()
        }
    }

    fun withDraw_Request() {
        commonFuncs.showLoadingDialog(this)
        val url = Constants.APIMain + "api/vendor-app/withdrawal"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.POST, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    commonFuncs.hideLoadingDialog()
                    commonFuncs.showDefaultDialog(this,"نجاح العملية","تم ارسال طلب سحب الرصيد")
                }, Response.ErrorListener { error ->
                    Log.e("Error", error.toString())
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(this,"فشل إرسال الطلب",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(this,"فشل إرسال الطلب","حصل خطأ ما")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()
                }) {
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String,String>()
                    params["type"] = type
                    params["paypal_email"] = paypal_email
                    params["bank_name"] = bank_name
                    params["IBAN"] = IBAN
                    params["amount"] = amount
                    return params
                }
                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String,String>()
                    if (commonFuncs.IsInSP(this@UserWithdrawScreen, Constants.KeyUserToken)){
                        header["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@UserWithdrawScreen,
                            Constants.KeyUserToken
                        )
                    }
                    return header
                }
            }
            val requestQueue = Volley.newRequestQueue(this  )
            requestQueue.add(stringRequest)
        }catch (error: JSONException){
            Log.e("Response", error.toString())
            commonFuncs.hideLoadingDialog()
        }
    }
}