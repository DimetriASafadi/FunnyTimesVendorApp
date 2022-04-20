package com.example.funnytimesvendorapp.SectionCalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funnytimesvendorapp.RecViews.CalendarDayRecView
import com.example.funnytimesvendorapp.databinding.TScreenCalendarBinding
import java.util.*
import kotlin.collections.ArrayList

class CalendarScreen : AppCompatActivity() {

    lateinit var binding:TScreenCalendarBinding
    val ftpCalendars = ArrayList<Calendar>()
    lateinit var calendarDayRecView:CalendarDayRecView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TScreenCalendarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.backBtn.setOnClickListener {
            finish()
        }

        calendarDayRecView = CalendarDayRecView(ftpCalendars,this)
        binding.CalendarDaysRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.CalendarDaysRecycler.adapter = calendarDayRecView
        binding.CalendarDaysRecycler.recycledViewPool.setMaxRecycledViews(TYPE_CAROUSEL, 0)

        FillCalendarList()






    }

    fun FillCalendarList(){

        var nowCal = Calendar.getInstance()
        nowCal.add(Calendar.DATE,-30)
        Log.e("Before30",nowCal.get(Calendar.DAY_OF_MONTH).toString())
        for (i in 0 until 60){
            Log.e("After30",nowCal.get(Calendar.DAY_OF_MONTH).toString())
            ftpCalendars.add(nowCal)
        }
        calendarDayRecView.notifyDataSetChanged()

    }
}