package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTPItemAttributes
import com.example.funnytimesvendorapp.R

class OrderItemsAttributesRecView (val data : ArrayList<FTPItemAttributes>, val context: Context) : RecyclerView.Adapter<OrderItemAttViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemAttViewHolder {
        return OrderItemAttViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_order_item_attributes, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: OrderItemAttViewHolder, position: Int) {


        holder.AttributesName.text = data[position].AttributeName
        holder.AttributesValue.text = data[position].AttributeValue

    }
}
class OrderItemAttViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val AttributesName = view.findViewById<TextView>(R.id.AttributesName)
    val AttributesValue = view.findViewById<TextView>(R.id.AttributesValue)


}