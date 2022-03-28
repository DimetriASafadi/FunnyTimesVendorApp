package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTPProductAttribute
import com.example.funnytimesvendorapp.R

class ProductAttributesRecView (val data : ArrayList<FTPProductAttribute>, val context: Context) : RecyclerView.Adapter<ProdAttrViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdAttrViewHolder {
        return ProdAttrViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_prop_attribute, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: ProdAttrViewHolder, position: Int) {

        holder.AttributeSelected.isChecked = data[position].IsSelected
        holder.AttributeName.text = data[position].AttributeName
        if (data[position].AttributeType == "value"){
            holder.AttributeValue.addTextChangedListener {
                data[position].AttributeValue = holder.AttributeValue.text.toString()
            }
        }else if (data[position].AttributeType == "check"){
            holder.AttributeValue.visibility = View.GONE
        }

    }
}
class ProdAttrViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val AttributeSelected = view.findViewById<CheckBox>(R.id.AttributeSelected)
    val AttributeName = view.findViewById<TextView>(R.id.AttributeName)
    val AttributeValue = view.findViewById<EditText>(R.id.AttributeValue)
}