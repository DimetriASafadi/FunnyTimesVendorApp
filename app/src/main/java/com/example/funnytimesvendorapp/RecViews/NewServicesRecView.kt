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
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.funnytimesvendorapp.Models.FTPNewService
import com.example.funnytimesvendorapp.R

class NewServicesRecView (val data : ArrayList<FTPNewService>, val context: Context) : RecyclerView.Adapter<NSerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NSerViewHolder {
        return NSerViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_new_service, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
    override fun onBindViewHolder(holder: NSerViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.ServiceDelete.setOnClickListener {
            data.removeAt(position)
            notifyDataSetChanged()
        }

        holder.ServiceName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                data[position].ServiceName = holder.ServiceName.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        holder.ServicePrice.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                data[position].ServicePrice = holder.ServicePrice.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    fun GetServices():ArrayList<FTPNewService>{
        notifyDataSetChanged()
        return data
    }
    fun CheckServiceIsEmpty():Boolean{
        for (service in data){
            if (service.ServiceName.isNullOrEmpty() || service.ServicePrice.isNullOrEmpty()){
                return true
            }
        }
        return false
    }

}
class NSerViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ServiceDelete = view.findViewById<ImageView>(R.id.ServiceDelete)
    val ServiceName = view.findViewById<EditText>(R.id.ServiceName)
    val ServicePrice = view.findViewById<EditText>(R.id.ServicePrice)
}