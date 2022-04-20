package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class CalendarDayRecView (val data : ArrayList<Calendar>, val context: Context) : RecyclerView.Adapter<CalDAyViewHolder>() {

     val VIEW_TYPE_SECTION = 1
     val VIEW_TYPE_ITEM = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalDAyViewHolder {
        return CalDAyViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_calendar_day, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        if (data[position] is SectionItem) {
            return VIEW_TYPE_SECTION
        }
        return VIEW_TYPE_ITEM
    }
    override fun onBindViewHolder(holder: CalDAyViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        val cal = data[position]
        cal.add(Calendar.DATE,position)

        val dayname = dayOfWeek(data[position])
        val daynumber = data[position].get(Calendar.DAY_OF_MONTH).toString()
        val daymonth = data[position].getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
        holder.CalDay.text = dayname
        holder.CalDayNum.text = daynumber
        holder.CalDayMonth.text = daymonth
        Log.e("RecViewCalendar",data[position].toString())

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

}
class CalDAyViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val CalDay = view.findViewById<TextView>(R.id.CalDay)
    val CalDaySelected = view.findViewById<CircleImageView>(R.id.CalDaySelected)
    val CalDayNum = view.findViewById<TextView>(R.id.CalDayNum)
    val CalDayMonth = view.findViewById<TextView>(R.id.CalDayMonth)
}