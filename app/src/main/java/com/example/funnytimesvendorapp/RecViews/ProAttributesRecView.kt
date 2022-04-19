package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTProAttribute
import com.example.funnytimesvendorapp.R

class ProAttributesRecView (val data : ArrayList<FTProAttribute>, val context: Context) : RecyclerView.Adapter<ProAttrViewHolder>() {

    var selectedItem = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProAttrViewHolder {
        return ProAttrViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_pro_attribute, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ProAttrViewHolder, position: Int) {

        holder.ProAttName.text = data[position].AttrName
        if (selectedItem == position){
            data[position].AttributeIsSelected = true
            holder.ProAttName.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.ft_dark_blue))
            holder.ProAttName.setTextColor(context.getColor(R.color.ft_white))
        }else{
            data[position].AttributeIsSelected = false
            holder.ProAttName.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.ft_grey_6))
            holder.ProAttName.setTextColor(context.getColor(R.color.ft_grey_7))
        }

        holder.ProAttName.setOnClickListener {
            selectedItem = position
            notifyDataSetChanged()
        }

    }
}
class ProAttrViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ProAttName = view.findViewById<TextView>(R.id.ProAttName)
}