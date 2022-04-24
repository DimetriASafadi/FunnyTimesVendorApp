package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTPTransaction
import com.example.funnytimesvendorapp.R


class TransactionsRecView (val data : ArrayList<FTPTransaction>, val context: Context) : RecyclerView.Adapter<TransViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        return TransViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_transaction, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        holder.TransProduct.text = data[position].TransName
        holder.TransDate.text = data[position].TransCreatedAt
        holder.TransValue.text = data[position].TransAmount


    }
}
class TransViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val TransProduct = view.findViewById<TextView>(R.id.TransProduct)
    val TransDate = view.findViewById<TextView>(R.id.TransDate)
    val TransValue = view.findViewById<TextView>(R.id.TransValue)
}