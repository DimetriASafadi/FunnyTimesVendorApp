package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.Models.FTItemPhoto
import com.example.funnytimesvendorapp.R

class ZoomableRecView(val data: ArrayList<FTItemPhoto>, val context: Context) : RecyclerView.Adapter<ZomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZomViewHolder {
        return ZomViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.rec_item_zommable_image,
                parent,
                false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ZomViewHolder, position: Int) {


        Glide.with(context)
            .load(data[position].PhoneUrl)
            .into(holder.theimg)

    }
}
class ZomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val theimg = view.findViewById<ImageView>(R.id.theimg)

}