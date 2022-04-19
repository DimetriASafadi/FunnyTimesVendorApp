package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.Models.FTAttribute
import com.example.funnytimesvendorapp.R


class AttributesRecView(val data : ArrayList<FTAttribute>, val context: Context) : RecyclerView.Adapter<AttributeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder {
        return AttributeViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_attribute, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: AttributeViewHolder, position: Int) {
//        holder?.image?.setOnClickListener {
//            Log.e("imagedata",data.get(position).PhotoName+"")
//        }
        Glide.with(context)
            .load(data[position].AttributeIcon)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.AttributeImage)
        holder.AttributeNameValue.text = data[position].AttributeValue.toString() + " " + data[position].AttributeName

    }
}
class AttributeViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val AttributeImage = view.findViewById<ImageView>(R.id.AttributeImage)
    val AttributeNameValue = view.findViewById<TextView>(R.id.AttributeNameValue)
}