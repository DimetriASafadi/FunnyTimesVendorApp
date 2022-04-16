package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.Models.FTPMyOrder
import com.example.funnytimesvendorapp.OrBokSection.FoodScreen
import com.example.funnytimesvendorapp.OrBokSection.ProductScreen
import com.example.funnytimesvendorapp.R
import com.makeramen.roundedimageview.RoundedImageView

class MyOrdRecView (val data : ArrayList<FTPMyOrder>, val context: Context): RecyclerView.Adapter<MyOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        return MyOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_orbok, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {


        Glide.with(context)
            .load(data[position].OrderItems[0].ItemDetails!!.ItemImg)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.OrBokImage)

        holder.OrBokName.text = data[position].OrderItems[0].ItemDetails!!.ItemName
        holder.OrBokId.text = data[position].OrderId.toString()
        holder.OrBokCreationDate.text = data[position].OrderCreatedAt.toString()
        holder.OrBokItemsCount.text = "("+data[position].OrderItems.size.toString()+")"
        holder.OrBokPrice.text = data[position].OrderTotal.toString()+" ر.س"

        if(data[position].OrderStatus == "completed"){
            holder.OrBokStatus.text = "مكتمل"
            holder.OrBokStatus.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_light_green,null))
            holder.OrBokStatus.setTextColor(context.resources.getColor(R.color.ft_green,null))
        }else if(data[position].OrderStatus == "pending"){
            holder.OrBokStatus.text = "معلق"
            holder.OrBokStatus.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_light_orange,null))
            holder.OrBokStatus.setTextColor(context.resources.getColor(R.color.ft_orange,null))
        }else if(data[position].OrderStatus == "unpaid"){
            holder.OrBokStatus.text = "غير مدفوع"
            holder.OrBokStatus.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_light_orange,null))
            holder.OrBokStatus.setTextColor(context.resources.getColor(R.color.ft_orange,null))
        }else if(data[position].OrderStatus == "canceled"){
            holder.OrBokStatus.text = "ملغي"
            holder.OrBokStatus.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_light_red,null))
            holder.OrBokStatus.setTextColor(context.resources.getColor(R.color.ft_red,null))
        }

        holder.OrBokCustomer.text = data[position].OrderCustomer.toString()
        holder.OrBokLocation.text = data[position].OrderLat.toString()+"|"+data[position].OrderLng.toString()

        holder.WholeOrBok.setOnClickListener {
            if (data[position].OrderItems[0].ItemDetails?.ItemType.toString() == "food"){
                context.startActivity(Intent(context, FoodScreen::class.java))
            }else{
                context.startActivity(Intent(context, ProductScreen::class.java))
            }
        }



    }
}
class MyOrderViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val WholeOrBok = view.findViewById<LinearLayout>(R.id.WholeOrBok)
    val OrBokImage = view.findViewById<RoundedImageView>(R.id.OrBokImage)
    val OrBokName = view.findViewById<TextView>(R.id.OrBokName)
    val OrBokId = view.findViewById<TextView>(R.id.OrBokId)
    val OrBokCreationDate = view.findViewById<TextView>(R.id.OrBokCreationDate)
    val OrBokItemsCount = view.findViewById<TextView>(R.id.OrBokItemsCount)
    val OrBokPrice = view.findViewById<TextView>(R.id.OrBokPrice)

    val OrBokStatus = view.findViewById<TextView>(R.id.OrBokStatus)
    val OrBokCustomer = view.findViewById<TextView>(R.id.OrBokCustomer)
    val OrBokLocation = view.findViewById<TextView>(R.id.OrBokLocation)

}