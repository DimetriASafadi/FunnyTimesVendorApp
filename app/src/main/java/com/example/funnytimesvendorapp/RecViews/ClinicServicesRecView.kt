package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTClinicService
import com.example.funnytimesvendorapp.R


class ClinicServicesRecView(val data : ArrayList<FTClinicService>, val context: Context) : RecyclerView.Adapter<serviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): serviceViewHolder {
        return serviceViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_clinic_service, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: serviceViewHolder, position: Int) {

        holder.ServiceName.text = data[position].ServiceName
        holder.ServicePrice.text = data[position].ServicePrice

        if (data[position].ServiceIsSelected){
            holder.IsSelected.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_green,null))
        }else{
            holder.IsSelected.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_grey_3,null))
        }

    }

    fun getFinalPrice():Double{
        var total = 0.0
        for (i in 0 until data.size) {
            if (data[i].ServiceIsSelected){
                total += data[i].ServicePrice!!.toDouble()
            }
        }
        return total
    }

    fun getselectedServices():ArrayList<FTClinicService>{
        val result = ArrayList<FTClinicService>()
        for (i in 0 until data.size) {
            if (data[i].ServiceIsSelected){
                result.add(data[i])
            }
        }
        return result
    }
}
class serviceViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ServiceName = view.findViewById<TextView>(R.id.ServiceName)
    val ServicePrice = view.findViewById<TextView>(R.id.ServicePrice)
    val IsSelected = view.findViewById<ImageView>(R.id.IsSelected)
    val WholeService = view.findViewById<LinearLayout>(R.id.WholeService)
}