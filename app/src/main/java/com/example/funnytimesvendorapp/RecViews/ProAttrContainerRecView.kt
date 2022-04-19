package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.funnytimesvendorapp.Models.FTProAttrContainer
import com.example.funnytimesvendorapp.R


class ProAttrContainerRecView(val data : ArrayList<FTProAttrContainer>, val context: Context) : RecyclerView.Adapter<AttrContViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttrContViewHolder {
        return AttrContViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_attributes_container, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: AttrContViewHolder, position: Int) {

        val chipsLayoutManager = ChipsLayoutManager.newBuilder(context)
            .setChildGravity(Gravity.CENTER_VERTICAL)
            .setScrollingEnabled(false)
            .setGravityResolver { Gravity.CENTER_VERTICAL }
            .setOrientation(ChipsLayoutManager.HORIZONTAL)
            .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
            .build()

        holder.AttContainerName.text = data[position].ContainerName
        val adapter = ProAttributesRecView(data[position].ContainerAttributes,context)
        holder.AttContainerRecycler.layoutManager = chipsLayoutManager
        holder.AttContainerRecycler.adapter = adapter
    }

    fun getAllData():ArrayList<FTProAttrContainer>{
        return data
    }



}
class AttrContViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val AttContainerName = view.findViewById<TextView>(R.id.AttContainerName)
    val AttContainerRecycler = view.findViewById<RecyclerView>(R.id.AttContainerRecycler)
}