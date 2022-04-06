package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.EditsSection.EditChaletScreen
import com.example.funnytimesvendorapp.EditsSection.EditClinicScreen
import com.example.funnytimesvendorapp.EditsSection.EditFoodScreen
import com.example.funnytimesvendorapp.EditsSection.EditProductScreen
import com.example.funnytimesvendorapp.Models.FTPMyItem
import com.example.funnytimesvendorapp.MyProductSection.MyProductScreen
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
        if (data[position].ItemReviews == null){
            holder.ItemReviews.visibility = View.GONE
        }else{
            holder.ItemReviews.visibility = View.VISIBLE
        }


        holder.WholeItem.setOnClickListener {
            if (data[position].ItemType == "booking"){
                val intent = Intent(context, EditChaletScreen::class.java)
                intent.putExtra("ItemId",data[position].ItemId.toString())
                context.startActivity(intent)
            }else if (data[position].ItemType == "food"){
                val intent = Intent(context, EditFoodScreen::class.java)
                intent.putExtra("ItemId",data[position].ItemId.toString())
                context.startActivity(intent)
            }else if (data[position].ItemType == "shop"){
                val intent = Intent(context, EditProductScreen::class.java)
                intent.putExtra("ItemId",data[position].ItemId.toString())
                context.startActivity(intent)
            }else if (data[position].ItemType == "service"){
                val intent = Intent(context, EditClinicScreen::class.java)
                intent.putExtra("ItemId",data[position].ItemId.toString())
                context.startActivity(intent)
            }
        }




    }
}
class myItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val WholeItem = view.findViewById<LinearLayout>(R.id.WholeItem)
    val ItemImage = view.findViewById<RoundedImageView>(R.id.ItemImage)
    val ItemName = view.findViewById<TextView>(R.id.ItemName)
    val ItemId = view.findViewById<TextView>(R.id.ItemId)
    val ItemCreationDate = view.findViewById<TextView>(R.id.ItemCreationDate)
    val ItemRate = view.findViewById<BaseRatingBar>(R.id.ItemRate)
    val ItemReviews = view.findViewById<TextView>(R.id.ItemReviews)

}