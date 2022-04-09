package com.example.funnytimesvendorapp.MainMenuSection.HomeSection

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
import com.example.funnytimesvendorapp.Models.FTPFoodType
import com.example.funnytimesvendorapp.Models.FTPMyItem
import com.example.funnytimesvendorapp.Models.FTPOrdBok
import com.example.funnytimesvendorapp.MyProductSection.MyProductScreen
import com.example.funnytimesvendorapp.RecViews.MyItemsRecView
import com.example.funnytimesvendorapp.RecViews.OrBokRecView
import com.example.funnytimesvendorapp.databinding.FtpFragHomeBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset


class HomeFrag: Fragment() {
    private var _binding: FtpFragHomeBinding? = null
    private val binding get() = _binding!!

    val commonFuncs = CommonFuncs()

    val fTPOrdBoks = ArrayList<FTPOrdBok>()
    lateinit var orBokRecView: OrBokRecView

    val ftpMyItem = ArrayList<FTPMyItem>()
    lateinit var myItemsRecView: MyItemsRecView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FtpFragHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.SalesChart.setDrawBarShadow(false)
        binding.SalesChart.setDrawValueAboveBar(true)
        binding.SalesChart.setMaxVisibleValueCount(50)
        val weekdays = arrayOf(
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat"
        ) // Your List / array with String Values For X-axis Labels
        val xAxis: XAxis = binding.SalesChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(weekdays)
        val values: ArrayList<BarEntry> = ArrayList()
            values.add(BarEntry(0.0f, 10.0f))
            values.add(BarEntry(1.0f, 20.0f))
            values.add(BarEntry(2.0f, 30.0f))
            values.add(BarEntry(3.0f, 40.0f))
            values.add(BarEntry(4.0f, 50.0f))
            values.add(BarEntry(5.0f, 60.0f))
            values.add(BarEntry(6.0f, 70.0f))
            values.add(BarEntry(7.0f, 80.0f))
            values.add(BarEntry(8.0f, 90.0f))
            values.add(BarEntry(9.0f, 50.0f))
            values.add(BarEntry(10.0f, 40.0f))
        val set1: BarDataSet
        if (binding.SalesChart.data != null &&
            binding.SalesChart.data.dataSetCount > 0
        ) {
            set1 = binding.SalesChart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            binding.SalesChart.data.notifyDataChanged()
            binding.SalesChart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "Data Set")
            set1.setColors(*ColorTemplate.VORDIPLOM_COLORS)
            set1.setDrawValues(false)
            val dataSets: ArrayList<IBarDataSet> = ArrayList()
            dataSets.add(set1)
            val data = BarData(dataSets)
            binding.SalesChart.data = data
            binding.SalesChart.setFitBars(true)
        }
        binding.SalesChart.invalidate()


        orBokRecView = OrBokRecView(fTPOrdBoks,requireContext())
        binding.OrBokRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.OrBokRecycler.adapter = orBokRecView

        myItemsRecView = MyItemsRecView(ftpMyItem,requireContext())
        binding.MyItemsRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.MyItemsRecycler.adapter = myItemsRecView

        Home_Request()


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun Home_Request() {
        commonFuncs.showLoadingDialog(requireActivity())
        val url = Constants.APIMain + "api/vendor-app/home?day=7"
        try {
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONObject("data")
                    val order = data.getJSONArray("order")
                    val product = data.getJSONArray("product")
                    val gson = GsonBuilder().create()
                    fTPOrdBoks.addAll(gson.fromJson(order.toString(),Array<FTPOrdBok>::class.java).toList())
                    ftpMyItem.addAll(gson.fromJson(product.toString(),Array<FTPMyItem>::class.java).toList())
                    orBokRecView.notifyDataSetChanged()
                    myItemsRecView.notifyDataSetChanged()
                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(requireContext(),"فشل الإتصال",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(requireContext(),"فشل الإتصال","تفقد إتصالك بالشبكة")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map = java.util.HashMap<String, String>()


                    if (commonFuncs.IsInSP(requireContext(), Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(requireContext(), Constants.KeyUserToken)
                    }
                    map["Authorization"] = "Bearer "+"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI5MTU5NWQ5ZS1iYjA5LTQ0MjMtOGY0YS0xZTdlMzI1ODBhNGQiLCJqdGkiOiJlYTFmYWNkNjAxZTMyYzJhZThlOTgwZjlkMGUxZjhiY2NlOTk4Mjk5OTVlNzJlZDU3OTMyMTRkOWFiNGI5MzU5MDcxNDhmMDk1ZGE5ZTk2MiIsImlhdCI6MTY0Nzc3Njg4OCwibmJmIjoxNjQ3Nzc2ODg4LCJleHAiOjE2NzkzMTI4ODgsInN1YiI6IjM3NCIsInNjb3BlcyI6W119.U_54ouTLkTMHfO6wVI_tys_suzB7gsZ4BcSQvn9PS4he1hV1PmLLAISlgmhHk_qTIyV__bLu4i5w6s7kj_U_1pmb_2UIjlUwZxvBu6GEWI5JV0BdLfdF81pByg3hSAy3IhHC-iRZ2VXOkWvhUKbzvrKMT9o96NA-NhsB3nDjqCCpcuKqsnN78-rKz7-AYHT3kx8WzR9ISXgGtAtbuGuPY0VOywv4OjZ7qZR821Jqstv6WU1okkiZtOer_I5eANuuTn7M5N00IHlj7aQj0v-Q9BViI2pCH4bukNGXLYH0Y4bp0hRwVgJGQLfJQIXGRdkxWc6eUrx7FeyoaNMmN737rDSFcICtrFQ9Lb3evPL_motCQsts8gCaLGhEnYTFM69gyisJsuA0bB756i3JFkJn5k5qhtffO0obDfChZSKV2qN0ITZxZNdPGOoNU5UIN0y_wuMPpwYYrRwPRxbDYfqTLB_8enaDDYVwOs8lXGrWyFIY3-HUZBqKIKehyyjVNaqEkCCV1fetvnLxO2zZZXGdc64CY0uX55FvyK0yIKPP5rdwk4SZ1ut8qiewTfWdpkDuRKaULm3dWymzOmx2Y8-5JxTeH8cTgvIxAxcEuaytodW7-BvAgff_pSyUpdYjvKB5iemD577OJFmjGn3nlPp39APPDdA85yzV6J4D5h7uGZ8"

                    return map
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