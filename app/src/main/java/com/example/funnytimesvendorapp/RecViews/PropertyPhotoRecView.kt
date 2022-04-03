package com.example.funnytimesvendorapp.RecViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.Models.FTPItemPhoto
import com.example.funnytimesvendorapp.R
import com.makeramen.roundedimageview.RoundedImageView
import de.hdodenhof.circleimageview.CircleImageView

class PropertyPhotoRecView (val data : ArrayList<FTPItemPhoto>, val context: Context) : RecyclerView.Adapter<PropPhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropPhotoViewHolder {
        return PropPhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_prop_photo, parent, false))    }

    private var deletedPhotos = ArrayList<Int>()


    override fun getItemCount(): Int {
        return data?.size ?: 0
    }


    override fun onBindViewHolder(holder: PropPhotoViewHolder, position: Int) {

        if (data[position].PhotoType == "new"){
            holder.PropertyImage.setImageURI(data[position].PhotoUri)
        }else{
            Glide.with(context)
                .load(data[position].PhotoUriString)
                .centerCrop()
                .placeholder(R.drawable.ft_broken_image)
                .into(holder.PropertyImage)
        }
        holder.ProductDelete.setOnClickListener {
            if (data[position].PhotoType != "new"){
                deletedPhotos.add(data[position].PhotoId!!)
            }
            data.removeAt(position)
            notifyDataSetChanged()
        }

    }

    fun getDeletedPhotos():ArrayList<Int>{
        return deletedPhotos
    }



}
class PropPhotoViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val PropertyImage = view.findViewById<RoundedImageView>(R.id.PropertyImage)
    val ProductDelete = view.findViewById<CircleImageView>(R.id.ProductDelete)
}