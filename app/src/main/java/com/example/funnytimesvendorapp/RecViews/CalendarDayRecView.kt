package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.InterFaces.OnCalClick
import com.example.funnytimesvendorapp.Models.FTPCalendar
import com.example.funnytimesvendorapp.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class CalendarDayRecView (val data : ArrayList<FTPCalendar>, val context: Context,val onCalClick: OnCalClick) : RecyclerView.Adapter<CalDAyViewHolder>() {


    var selecteditem = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalDAyViewHolder {
        return CalDAyViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_calendar_day, parent, false))    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CalDAyViewHolder, position: Int) {

        data[position].CalIsSelected = selecteditem == position

        if (data[position].CalIsSelected){
            holder.CalDaySelected.setImageResource(R.color.ft_orange)
            holder.CalDayNum.setTextColor(context.resources.getColor(R.color.ft_white,null))
        }else{
            holder.CalDaySelected.setImageResource(R.color.ft_white)
            holder.CalDayNum.setTextColor(context.resources.getColor(R.color.tk_weak_black,null))
        }
        holder.CalDay.text = data[position].CalDayName
        holder.CalDayNum.text = data[position].CalDay.toString()
        holder.CalDayMonth.text = data[position].CalMonthName

        holder.WholeCalendar.setOnClickListener{
            selecteditem = position
            onCalClick.OnCalClickListener(data[position])
            notifyDataSetChanged()
        }

    }



}
class CalDAyViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val WholeCalendar = view.findViewById<LinearLayout>(R.id.WholeCalendar)
    val CalDay = view.findViewById<TextView>(R.id.CalDay)
    val CalDaySelected = view.findViewById<CircleImageView>(R.id.CalDaySelected)
    val CalDayNum = view.findViewById<TextView>(R.id.CalDayNum)
    val CalDayMonth = view.findViewById<TextView>(R.id.CalDayMonth)
}