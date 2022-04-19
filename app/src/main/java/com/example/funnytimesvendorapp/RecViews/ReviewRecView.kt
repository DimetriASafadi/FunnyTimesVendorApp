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

class ReviewRecView(val data : ArrayList<FTReview>, val context: Context) : RecyclerView.Adapter<ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_review, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
//        holder?.image?.setOnClickListener {
//            Log.e("imagedata",data.get(position).PhotoName+"")
//        }
        Glide.with(context)
            .load(data[position].ReviewUserImg)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.ReviewOwnerImage)
        holder.ReviewOwnerName.text = data[position].ReviewUserName
        holder.ReviewDate.text = data[position].ReviewDate
        holder.ReviewComment.text = data[position].ReviewComment
        holder.ReviewRate.rating = data[position].ReviewRate!!.toFloat()
        holder.ReviewRate.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }

    }
}
class ReviewViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ReviewOwnerImage = view.findViewById<CircleImageView>(R.id.ReviewOwnerImage)
    val ReviewOwnerName = view.findViewById<TextView>(R.id.ReviewOwnerName)
    val ReviewDate = view.findViewById<TextView>(R.id.ReviewDate)
    val ReviewComment = view.findViewById<TextView>(R.id.ReviewComment)
    val ReviewRate = view.findViewById<BaseRatingBar>(R.id.ReviewRate)
}