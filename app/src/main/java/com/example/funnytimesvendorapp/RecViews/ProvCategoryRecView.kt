package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.InterFaces.OnCategoryClick
import com.example.funnytimesvendorapp.Models.FTPCategory
import com.example.funnytimesvendorapp.R

class ProvCategoryRecView(val data : ArrayList<FTPCategory>, val context: Context,val onCategoryClick: OnCategoryClick) : RecyclerView.Adapter<BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_provi_category, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {

        Glide.with(context)
            .load(data[position].CategoryImg)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.CategoryImg)

        holder.CategoryName.text = data[position].CategoryName

        holder.WholeCategory.setOnClickListener {
            onCategoryClick.OnCategoryClickListener(data[position])
        }

    }
}
class BannerViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val WholeCategory = view.findViewById<LinearLayout>(R.id.WholeCategory)
    val CategoryImg = view.findViewById<ImageView>(R.id.CategoryImg)
    val CategoryName = view.findViewById<TextView>(R.id.CategoryName)
}