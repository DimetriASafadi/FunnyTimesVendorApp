package com.example.funnytimesvendorapp.SectionCalendar

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.funnytimesvendorapp.CommonSection.CommonFuncs
import com.example.funnytimesvendorapp.CommonSection.Constants
import com.example.funnytimesvendorapp.InterFaces.OnCalClick
import com.example.funnytimesvendorapp.Models.FTPCalendar
import com.example.funnytimesvendorapp.Models.FTPCalendarContent
import com.example.funnytimesvendorapp.RecViews.CalContentRecView
import com.example.funnytimesvendorapp.RecViews.CalendarDayRecView
import com.example.funnytimesvendorapp.databinding.TScreenCalendarBinding
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class CalendarScreen : AppCompatActivity(), OnCalClick {

    lateinit var binding:TScreenCalendarBinding
    val ftpCalendars = ArrayList<FTPCalendar>()
    lateinit var calendarDayRecView:CalendarDayRecView

    val commonFuncs = CommonFuncs()

    val ftpCalendarContent = ArrayList<FTPCalendarContent>()
    lateinit var calContentRecView:CalContentRecView

    var selecteddate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TScreenCalendarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.backBtn.setOnClickListener {
            finish()
        }

        calendarDayRecView = CalendarDayRecView(ftpCalendars,this,this)
        binding.CalendarDaysRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.CalendarDaysRecycler.adapter = calendarDayRecView

        calContentRecView = CalContentRecView(ftpCalendarContent,this)
        binding.CalContentRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.CalContentRecycler.adapter = calContentRecView

        FillCalendarList()



    }

    override fun OnCalClickListener(ftpCalendar: FTPCalendar) {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val selectdate = Date(ftpCalendar.CalLong!!)
        val dateString: String = formatter.format(selectdate)
        selecteddate = dateString
        Log.e("dateString",dateString)
        Calendar_Content_Request()
    }

    fun FillCalendarList(){

        var nowCal = Calendar.getInstance()
        nowCal.add(Calendar.DATE,-30)
        Log.e("Before30",nowCal.get(Calendar.DAY_OF_MONTH).toString())
        for (i in 0 until 60){
            nowCal.add(Calendar.DATE,1)
            Log.e("After30",nowCal.get(Calendar.DAY_OF_MONTH).toString())
            ftpCalendars.add(FTPCalendar(nowCal.timeInMillis,nowCal.get(Calendar.DAY_OF_MONTH),dayOfWeek(nowCal),
                nowCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
            ,false))
        }
        calendarDayRecView.notifyDataSetChanged()

        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val selectdate = Date(ftpCalendars[0].CalLong!!)
        val dateString: String = formatter.format(selectdate)
        selecteddate = dateString
        Log.e("dateString",dateString)
        Calendar_Content_Request()

    }

    fun dayOfWeek(calendar: Calendar):String {
        println("What day is it today?")
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        return when (day) {
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            7 -> "Sat"
            else -> ""
        }
    }

    fun Calendar_Content_Request(){
        var url = Constants.APIMain + "api/vendor-app/booking?date=$selecteddate"
        Log.e("Calendar_Content_Request",url)
        try {
            commonFuncs.showLoadingDialog(this)
            val stringRequest = object : StringRequest(
                Request.Method.GET, url, Response.Listener<String> { response ->
                    Log.e("Response", response.toString())
                    val jsonobj = JSONObject(response.toString())
                    val data = jsonobj.getJSONArray("data")
                    val gson = GsonBuilder().create()
                    ftpCalendarContent.clear()
                    ftpCalendarContent.addAll(gson.fromJson(data.toString(),Array<FTPCalendarContent>::class.java).toList())
                    calContentRecView.notifyDataSetChanged()
                    if (ftpCalendarContent.size == 0){
                        binding.CalContentIsEmpty.visibility = View.VISIBLE
                        binding.CalContentRecycler.visibility = View.INVISIBLE
                    }else{
                        binding.CalContentIsEmpty.visibility = View.INVISIBLE
                        binding.CalContentRecycler.visibility = View.VISIBLE
                    }
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
                    if (commonFuncs.IsInSP(this@CalendarScreen, Constants.KeyUserToken)){
                        params["Authorization"] = "Bearer "+commonFuncs.GetFromSP(this@CalendarScreen, Constants.KeyUserToken)
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