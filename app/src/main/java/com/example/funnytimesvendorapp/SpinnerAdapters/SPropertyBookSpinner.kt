package com.example.funnytimesvendorapp.SpinnerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.funnytimesvendorapp.Models.FTPPropertyBook
import com.example.funnytimesvendorapp.R

class SPropertyBookSpinner (val context: Context, var data: ArrayList<FTPPropertyBook>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.ftp_spinner_mini_filter_sorts, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.SortName.text = data[position].BookName


        return view
    }

    override fun getItem(position: Int): FTPPropertyBook? {
        return data[position];
    }

    override fun getCount(): Int {
        return data.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(view: View?) {
        val SortName = view?.findViewById(R.id.SortName) as TextView
    }

}