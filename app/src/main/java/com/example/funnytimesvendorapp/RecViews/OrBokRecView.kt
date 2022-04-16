package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.Models.FTPOrdBok
import com.example.funnytimesvendorapp.OrBokSection.FoodScreen
import com.example.funnytimesvendorapp.OrBokSection.ProductScreen
import com.example.funnytimesvendorapp.R
import com.makeramen.roundedimageview.RoundedImageView

class OrBokRecView (val data : ArrayList<FTPOrdBok>, val context: Context): RecyclerView.Adapter<OrBokViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrBokViewHolder {
        return OrBokViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_pending_book_order, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: OrBokViewHolder, position: Int) {


        Glide.with(context)
            .load(data[position].ObImg)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.OrBokImage)
        holder.OrBokName.text = data[position].ObName
        holder.OrBokId.text = data[position].ObId.toString()
        holder.OrBokCreationDate.text = data[position].ObCreatedAt.toString()

        holder.WholeOrBok.setOnClickListener {
            if (data[position].ObType == "food"){
                context.startActivity(Intent(context,FoodScreen::class.java))
            }else{
                context.startActivity(Intent(context,ProductScreen::class.java))
            }
        }




    }
}
class OrBokViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val WholeOrBok = view.findViewById<LinearLayout>(R.id.WholeOrBok)
    val OrBokImage = view.findViewById<RoundedImageView>(R.id.OrBokImage)
    val OrBokName = view.findViewById<TextView>(R.id.OrBokName)
    val OrBokId = view.findViewById<TextView>(R.id.OrBokId)
    val OrBokCreationDate = view.findViewById<TextView>(R.id.OrBokCreationDate)
    val OrBokAccept = view.findViewById<ImageView>(R.id.OrBokAccept)
    val OrBokDecline = view.findViewById<ImageView>(R.id.OrBokDecline)

}