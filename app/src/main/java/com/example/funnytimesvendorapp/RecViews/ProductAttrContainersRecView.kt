package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTPProductAttributeContainer
import com.example.funnytimesvendorapp.R

class ProductAttrContainersRecView (val data : ArrayList<FTPProductAttributeContainer>, val context: Context) : RecyclerView.Adapter<ProdAttrContainerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdAttrContainerViewHolder {
        return ProdAttrContainerViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_prop_attribute_container, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ProdAttrContainerViewHolder, position: Int) {

        holder.ContainerName.text = data[position].ContainerName
        holder.ContainerAttributesRecycler.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,
            false)
        val productAttributesRecView = ProductAttributesRecView(data[position].ContainerAttributes!!,context)
        holder.ContainerAttributesRecycler.adapter = productAttributesRecView

    }
}
class ProdAttrContainerViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ContainerName = view.findViewById<TextView>(R.id.ContainerName)
    val ContainerAttributesRecycler = view.findViewById<RecyclerView>(R.id.ContainerAttributesRecycler)
}