package com.example.funnytimesvendorapp.RecViews

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTPPropertyAttribute
import com.example.funnytimesvendorapp.R

class PropertyAttributesRecView (val data : ArrayList<FTPPropertyAttribute>, val context: Context) : RecyclerView.Adapter<PropAttrViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropAttrViewHolder {
        return PropAttrViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_prop_attribute, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: PropAttrViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.AttributeSelected.isChecked = data[position].IsSelected
        holder.AttributeName.text = data[position].AttributeName
        if (data[position].AttributeType == "value"){
            holder.AttributeValue.addTextChangedListener {
                    data[position].AttributeValue = holder.AttributeValue.text.toString()
            }
        }else if (data[position].AttributeType == "check"){
            holder.AttributeValue.visibility = View.GONE
        }

        holder.AttributeValue.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                data[position].AttributeValue = holder.AttributeValue.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
    fun CheckAttributesEmpty():Boolean{
            for (attr in data){
                if (attr.AttributeType == "value"){
                    if ( attr.IsSelected && attr.AttributeValue.isNullOrEmpty()){
                        return true
                    }
                }

            }
            return false
    }


}
class PropAttrViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val AttributeSelected = view.findViewById<CheckBox>(R.id.AttributeSelected)
    val AttributeName = view.findViewById<TextView>(R.id.AttributeName)
    val AttributeValue = view.findViewById<EditText>(R.id.AttributeValue)
}