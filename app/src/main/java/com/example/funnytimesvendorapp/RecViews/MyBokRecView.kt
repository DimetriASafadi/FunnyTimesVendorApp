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
import com.example.funnytimesvendorapp.Models.FTPMyBook
import com.example.funnytimesvendorapp.OrBokSection.OrderScreen
import com.example.funnytimesvendorapp.OrBokSection.ServiceScreen
import com.example.funnytimesvendorapp.R
import com.makeramen.roundedimageview.RoundedImageView

class MyBokRecView (val data : ArrayList<FTPMyBook>, val context: Context): RecyclerView.Adapter<MyBookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookViewHolder {
        return MyBookViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_orbok, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: MyBookViewHolder, position: Int) {


        Glide.with(context)
            .load(data[position].BookImg)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.OrBokImage)

        holder.OrBokName.text = data[position].BookName
        holder.OrBokId.text = "حجز رقم : "+data[position].BookId.toString()
        holder.OrBokCreationDate.text = data[position].BookCreatedAt.toString()
        holder.OrBokItemsCount.visibility = View.GONE
        holder.OrBokPrice.text = data[position].BookTotal.toString()+" ر.س"


        if(data[position].BookStatus == "completed"){
            holder.OrBokStatus.text = "مكتمل"
            holder.OrBokStatus.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_light_green,null))
            holder.OrBokStatus.setTextColor(context.resources.getColor(R.color.ft_green,null))
        }else if(data[position].BookStatus == "pending"){
            holder.OrBokStatus.text = "معلق"
            holder.OrBokStatus.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_light_orange,null))
            holder.OrBokStatus.setTextColor(context.resources.getColor(R.color.ft_orange,null))
        }else if(data[position].BookStatus == "unpaid"){
            holder.OrBokStatus.text = "غير مدفوع"
            holder.OrBokStatus.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_light_orange,null))
            holder.OrBokStatus.setTextColor(context.resources.getColor(R.color.ft_orange,null))
        }else if(data[position].BookStatus == "canceled"){
            holder.OrBokStatus.text = "ملغي"
            holder.OrBokStatus.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.ft_light_red,null))
            holder.OrBokStatus.setTextColor(context.resources.getColor(R.color.ft_red,null))
        }

        holder.OrBokCustomer.text = data[position].BookUsername.toString()
        holder.OrBokLocation.text = data[position].BookAddress.toString()

        holder.WholeOrBok.setOnClickListener {
            val intent = Intent(context, ServiceScreen::class.java)
            intent.putExtra("itemid",data[position].BookId.toString())
            context.startActivity(intent)
        }



    }
}
class MyBookViewHolder (view: View) : RecyclerView.ViewHolder(view) {
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