package com.example.funnytimesvendorapp.MainMenuSection.UserSection

import android.content.Intent
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
import com.example.funnytimesvendorapp.Models.FTPTransaction
import com.example.funnytimesvendorapp.Models.FTReview
import com.example.funnytimesvendorapp.RecViews.MyReviewsRecView
import com.example.funnytimesvendorapp.RecViews.TransactionsRecView
import com.example.funnytimesvendorapp.databinding.FtpFragUserBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class UserFrag: Fragment() {
    private var _binding: FtpFragUserBinding? = null
    private val binding get() = _binding!!

    val commonFuncs = CommonFuncs()


    val ftpTransactions = ArrayList<FTPTransaction>()
    lateinit var transactionsRecView: TransactionsRecView


    val ftReviews = ArrayList<FTReview>()
    lateinit var myReviewRecView: MyReviewsRecView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FtpFragUserBinding.inflate(inflater, container, false)
        val view = binding.root

        transactionsRecView = TransactionsRecView(ftpTransactions,requireContext())
        binding.TransactionsRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.TransactionsRecycler.adapter = transactionsRecView



        myReviewRecView = MyReviewsRecView(ftReviews,requireContext())
        binding.ReviewsRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.ReviewsRecycler.adapter = myReviewRecView

        binding.MoreReviews.setOnClickListener {
            startActivity(Intent(requireContext(),UserReviewsScreen::class.java))
        }
        binding.MoreTransactions.setOnClickListener {
            startActivity(Intent(requireContext(),UserTransactionScreen::class.java))
        }
        binding.UserEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(),UserEditProfileScreen::class.java))
        }

        getUserProfile_Request()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun getUserProfile_Request(){
        var url = Constants.APIMain + "api/vendor-app/user/page"

        try {
            commonFuncs.showLoadingDialog(requireActivity())
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val transaction = data.getJSONArray("transaction")
                    val reviews = data.getJSONArray("reviews")
                    val gson = GsonBuilder().create()
                    ftpTransactions.clear()
                    ftReviews.clear()
                    ftpTransactions.addAll(gson.fromJson(transaction.toString(),Array<FTPTransaction>::class.java).toList())
                    ftReviews.addAll(gson.fromJson(reviews.toString(),Array<FTReview>::class.java).toList())
                    transactionsRecView.notifyDataSetChanged()
                    myReviewRecView.notifyDataSetChanged()

//                    Glide.with(context)
//                        .load(data[position].ReviewUserImg)
//                        .centerCrop()
//                        .placeholder(R.drawable.ft_broken_image)
//                        .into(binding.UserImage)

                    var userdata = data.getJSONObject("userDate")
                    binding.VendorName.text = userdata.getString("name")

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