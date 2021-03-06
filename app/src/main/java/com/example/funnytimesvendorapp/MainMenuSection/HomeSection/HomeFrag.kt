package com.example.funnytimesvendorapp.MainMenuSection.HomeSection

import android.content.Intent
import android.graphics.Paint
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
import com.example.funnytimesvendorapp.Models.FTPMyItem
import com.example.funnytimesvendorapp.Models.FTPOrBokBar
import com.example.funnytimesvendorapp.Models.FTPOrdBok
import com.example.funnytimesvendorapp.MyProductSection.MyProductScreen
import com.example.funnytimesvendorapp.R
import com.example.funnytimesvendorapp.RecViews.MyItemsRecView
import com.example.funnytimesvendorapp.RecViews.OrBokRecView
import com.example.funnytimesvendorapp.SectionCalendar.CalendarScreen
import com.example.funnytimesvendorapp.databinding.FtpFragHomeBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
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

    val ftpOrBokBar = ArrayList<FTPOrBokBar>()

    val xdata = ArrayList<String>()
    val ydata = ArrayList<BarEntry>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FtpFragHomeBinding.inflate(inflater, container, false)
        val view = binding.root



        orBokRecView = OrBokRecView(fTPOrdBoks,requireContext(),"service")
        binding.OrBokRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.OrBokRecycler.adapter = orBokRecView

        myItemsRecView = MyItemsRecView(ftpMyItem,requireContext())
        binding.MyItemsRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.MyItemsRecycler.adapter = myItemsRecView

        binding.CalendarBtn.setOnClickListener {
            startActivity(Intent(requireContext(),CalendarScreen::class.java))
        }

        binding.AllMyItems.paintFlags = binding.AllMyItems.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.AllMyItems.setOnClickListener {
            startActivity(Intent(requireContext(),MyProductScreen::class.java))
        }

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
                    val bookingCount = data.getJSONArray("bookingCount")
                    val order = data.getJSONArray("order")
                    val booking = data.getJSONArray("booking")
                    val product = data.getJSONArray("product")
                    val gson = GsonBuilder().create()
                    fTPOrdBoks.addAll(gson.fromJson(booking.toString(),Array<FTPOrdBok>::class.java).toList())
                    ftpMyItem.addAll(gson.fromJson(product.toString(),Array<FTPMyItem>::class.java).toList())
                    ftpOrBokBar.addAll(gson.fromJson(bookingCount.toString(),Array<FTPOrBokBar>::class.java).toList())
                    xdata.addAll(GetXLineData())
                    ydata.addAll(GetYLineData())

                    SetUpChart()

                    orBokRecView.notifyDataSetChanged()
                    myItemsRecView.notifyDataSetChanged()
                    commonFuncs.hideLoadingDialog()
                }, Response.ErrorListener { error ->
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        val errorw = String(error.networkResponse.data, Charset.forName("UTF-8"))
                        val err = JSONObject(errorw)
                        val errMessage = err.getJSONObject("status").getString("message")
                        commonFuncs.showDefaultDialog(requireContext(),"?????? ??????????????",errMessage)
                        Log.e("eResponser", errorw.toString())
                    } else {
                        commonFuncs.showDefaultDialog(requireContext(),"?????? ??????????????","???????? ???????????? ??????????????")
                        Log.e("eResponsew", "RequestError:$error")
                    }
                    commonFuncs.hideLoadingDialog()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    if (commonFuncs.IsInSP(requireContext(), Constants.KeyUserToken)){
                        map["Authorization"] = "Bearer "+commonFuncs.GetFromSP(requireContext(), Constants.KeyUserToken)
                    }
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


    fun SetUpChart(){

        binding.SalesChart.setTouchEnabled(true)
        binding.SalesChart.isClickable = false
        binding.SalesChart.isDoubleTapToZoomEnabled = false
        binding.SalesChart.isDoubleTapToZoomEnabled = false

        binding.SalesChart.setDrawBorders(false)
        binding.SalesChart.setDrawGridBackground(false)

        binding.SalesChart.description.isEnabled = false
        binding.SalesChart.legend.isEnabled = false

        binding.SalesChart.axisLeft.setDrawGridLines(false)
        binding.SalesChart.axisLeft.setDrawLabels(true)
        binding.SalesChart.axisLeft.setDrawAxisLine(false)

        binding.SalesChart.xAxis.setDrawGridLines(false)
        binding.SalesChart.xAxis.setDrawLabels(true)
        binding.SalesChart.xAxis.setDrawAxisLine(false)

        binding.SalesChart.axisRight.setDrawGridLines(false)
        binding.SalesChart.axisRight.setDrawLabels(true)
        binding.SalesChart.axisRight.setDrawAxisLine(false)


        binding.SalesChart.setDrawBarShadow(false)
        binding.SalesChart.setDrawValueAboveBar(false)
        val xAxis: XAxis = binding.SalesChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xdata)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = xdata.size.toFloat()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.granularity = 1f

        xAxis.isGranularityEnabled = true

        xAxis.textColor = resources.getColor(R.color.ft_black,null)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        val set1: BarDataSet
        if (binding.SalesChart.data != null &&
            binding.SalesChart.data.dataSetCount > 0
        ) {
            set1 = binding.SalesChart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = ydata
          //  set1.setColors(resources.getColor(R.color.ft_orange,null))
            set1.color = resources.getColor(R.color.ft_orange,null)
            binding.SalesChart.data.notifyDataChanged()
            binding.SalesChart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(ydata, "Data Set")
          //  set1.setColors(resources.getColor(R.color.ft_orange,null))
            set1.color = resources.getColor(R.color.ft_orange,null)
            set1.setDrawValues(false)
            val dataSets: ArrayList<IBarDataSet> = ArrayList()
            dataSets.add(set1)
            val data = BarData(dataSets)
            binding.SalesChart.data = data
            binding.SalesChart.setFitBars(true)
        }
        binding.SalesChart.setTouchEnabled(true)
        binding.SalesChart.setPinchZoom(false)
        binding.SalesChart.isDoubleTapToZoomEnabled = false
        binding.SalesChart.setScaleEnabled(false)
        binding.SalesChart.invalidate()

    }

    fun GetXLineData():ArrayList<String>{
        val result = ArrayList<String>()
        for (i in 0 until ftpOrBokBar.size) {
            result.add(ftpOrBokBar[i].BarMonths.toString())
        }
        return result
    }
    fun GetYLineData():ArrayList<BarEntry>{
        val result = ArrayList<BarEntry>()
        for (i in 0 until ftpOrBokBar.size) {
            result.add(BarEntry(i.toFloat(), ftpOrBokBar[i].BarSum!!.toFloat()))
        }
        return result
    }


}