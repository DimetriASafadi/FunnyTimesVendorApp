package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTPCalendarContent
import com.example.funnytimesvendorapp.OrBokSection.ServiceScreen
import com.example.funnytimesvendorapp.R

class CalContentRecView (val data : ArrayList<FTPCalendarContent>, val context: Context) : RecyclerView.Adapter<CalContViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalContViewHolder {
        return CalContViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_calendar_content, parent, false))    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CalContViewHolder, position: Int) {


        holder.BookCustomer.text = data[position].ContentUserName
        holder.BookProperty.text = data[position].ContentName.toString()


        if (data[position].ContentBookingType == 1){
            holder.BookSystem.text = data[position].ContentStartDate + " - "+ data[position].ContentEndDate
        }else if (data[position].ContentBookingType == 2){
            holder.BookSystem.text = data[position].ContentStartDate + " ( "+ data[position].ContentStartTime + "-"+ data[position].ContentEndTime +")"
        }else if(data[position].ContentBookingType == 3){
            if (data[position].ContentPeriod == 1){
                holder.BookSystem.text = data[position].ContentStartDate + " ( صباحاً )"
            }else{
                holder.BookSystem.text = data[position].ContentStartDate + " ( مساءاً )"
            }
        }else if (data[position].ContentBookingType == 4){
            holder.BookSystem.text = data[position].ContentStartDate + " ( "+ data[position].ContentStartTime +" ) "
        }

        holder.WholeCalContent.setOnClickListener {
            val intent = Intent(context, ServiceScreen::class.java)
            intent.putExtra("itemid",data[position].ContentId.toString())
            context.startActivity(intent)
        }

    }



}
class CalContViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val WholeCalContent = view.findViewById<LinearLayout>(R.id.WholeCalContent)
    val BookCustomer = view.findViewById<TextView>(R.id.BookCustomer)
    val BookProperty = view.findViewById<TextView>(R.id.BookProperty)
    val BookSystem = view.findViewById<TextView>(R.id.BookSystem)
}