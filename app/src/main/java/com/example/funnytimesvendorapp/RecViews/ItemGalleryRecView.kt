package com.example.funnytimesvendorapp.RecViews

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.example.funnytimesvendorapp.Models.FTItemPhoto
import com.example.funnytimesvendorapp.R
import com.makeramen.roundedimageview.RoundedImageView

class ItemGalleryRecView(val data : ArrayList<FTItemPhoto>, val context: Context) : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.rec_item_gallery_photo, parent, false))    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {


        Glide.with(context)
            .load(data[position].PhoneUrl)
            .centerCrop()
            .placeholder(R.drawable.ft_broken_image)
            .into(holder.GalleryPhoto)


        holder.GalleryPhoto.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.ft_zoomable_images_dialog)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            val theslider: RecyclerView = dialog.findViewById(R.id.clickonme)
            val CloseGallery = dialog.findViewById<ImageView>(R.id.CloseGallery)
            CloseGallery.setOnClickListener { dialog.dismiss() }
            val layoutManager2: RecyclerView.LayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            theslider.setHasFixedSize(true)
            theslider.layoutManager = layoutManager2
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(theslider)
            val zoomableRecView = ZoomableRecView(data, context)
            theslider.adapter = zoomableRecView
            dialog.show()
            theslider.scrollToPosition(position)
            val window = dialog.window
            window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        }


    }
}
class PhotoViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val GalleryPhoto = view.findViewById<RoundedImageView>(R.id.GalleryPhoto)
}