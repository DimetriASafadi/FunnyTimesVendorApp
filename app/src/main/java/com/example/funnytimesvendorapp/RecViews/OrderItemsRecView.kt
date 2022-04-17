package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTPMyOrderItem
import com.example.funnytimesvendorapp.R


class OrderItemsRecView (val data : ArrayList<FTPMyOrderItem>, val context: Context) : RecyclerView.Adapter<OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_ordbok_details, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {


        holder.ItemName.text = data[position].ItemDetails?.ItemName.toString() + " ("+data[position].ItemTotal+" ريال"+")"
        holder.ItemQuantity.text = data[position].ItemQuantity.toString()
        holder.ItemType.text = data[position].ItemDetails?.ItemType.toString()
        holder.ItemPrice.text = data[position].ItemPrice
        holder.ItemCreatedAt.text = data[position].ItemDetails?.ItemCreatedAt.toString()
        holder.ItemPaymentMethod.text = "تحويل بنكي"



        val orderItemsAttributesRecView = OrderItemsAttributesRecView(data[position].ItemAttributes,context)
        holder.ItemAttributesRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        holder.ItemAttributesRecycler.adapter = orderItemsAttributesRecView




    }
}
class OrderItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ItemName = view.findViewById<TextView>(R.id.ItemName)
    val ItemQuantity = view.findViewById<TextView>(R.id.ItemQuantity)
    val ItemType = view.findViewById<TextView>(R.id.ItemType)
    val ItemPrice = view.findViewById<TextView>(R.id.ItemPrice)
    val ItemCreatedAt = view.findViewById<TextView>(R.id.ItemCreatedAt)
    val ItemPaymentMethod = view.findViewById<TextView>(R.id.ItemPaymentMethod)
    val ItemAttributesRecycler = view.findViewById<RecyclerView>(R.id.ItemAttributesRecycler)

}