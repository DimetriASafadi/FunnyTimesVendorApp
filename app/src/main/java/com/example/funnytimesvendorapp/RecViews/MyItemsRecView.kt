package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.Models.FTPMyItem
import com.example.funnytimesvendorapp.R
import com.makeramen.roundedimageview.RoundedImageView
import com.willy.ratingbar.BaseRatingBar

class MyItemsRecView (val data : ArrayList<FTPMyItem>, val context: Context) : RecyclerView.Adapter<myItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myItemViewHolder {
        return myItemViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_my_product, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: myItemViewHolder, position: Int) {


        Glide.with(context)
            .load(data[position].ItemImg)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.ItemImage)
        holder.ItemName.text = data[position].ItemName
        holder.ItemId.text = data[position].ItemId.toString()
        holder.ItemCreationDate.text = data[position].ItemCreateAt
        holder.ItemReviews.text = data[position].ItemReviews.toString()
        holder.ItemRate.rating = data[position].ItemRate!!.toFloat()



    }
}
class myItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ItemImage = view.findViewById<RoundedImageView>(R.id.ItemImage)
    val ItemName = view.findViewById<TextView>(R.id.ItemName)
    val ItemId = view.findViewById<TextView>(R.id.ItemId)
    val ItemCreationDate = view.findViewById<TextView>(R.id.ItemCreationDate)
    val ItemRate = view.findViewById<BaseRatingBar>(R.id.ItemRate)
    val ItemReviews = view.findViewById<TextView>(R.id.ItemReviews)

}