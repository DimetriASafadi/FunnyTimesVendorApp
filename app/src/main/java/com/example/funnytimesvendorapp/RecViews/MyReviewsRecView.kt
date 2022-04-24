package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.Models.FTReview
import com.example.funnytimesvendorapp.R
import com.willy.ratingbar.BaseRatingBar
import de.hdodenhof.circleimageview.CircleImageView

class MyReviewsRecView (val data : ArrayList<FTReview>, val context: Context) : RecyclerView.Adapter<myReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myReviewViewHolder {
        return myReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_my_review, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: myReviewViewHolder, position: Int) {
//        holder?.image?.setOnClickListener {
//            Log.e("imagedata",data.get(position).PhotoName+"")
//        }
        Glide.with(context)
            .load(data[position].ReviewUserImg)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.ReviewCustomerImage)
        holder.ReviewCustomerName.text = data[position].ReviewUserName
        holder.ReviewCustomerComment.text = data[position].ReviewComment
        holder.ReviewCustomerRate.rating = data[position].ReviewRate!!.toFloat()
        holder.ReviewCustomerRate.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }

    }
}
class myReviewViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ReviewCustomerImage = view.findViewById<CircleImageView>(R.id.ReviewCustomerImage)
    val ReviewCustomerName = view.findViewById<TextView>(R.id.ReviewCustomerName)
    val ReviewCustomerComment = view.findViewById<TextView>(R.id.ReviewCustomerComment)
    val ReviewCustomerRate = view.findViewById<BaseRatingBar>(R.id.ReviewCustomerRate)
}