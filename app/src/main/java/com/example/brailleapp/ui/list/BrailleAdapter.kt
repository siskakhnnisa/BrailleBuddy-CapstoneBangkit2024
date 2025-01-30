package com.example.brailleapp.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.brailleapp.R
import com.example.brailleapp.retrofit.ImageItem

class BrailleAdapter(private var images: List<ImageItem>): RecyclerView.Adapter<BrailleAdapter.BrailleViewHolder>() {
    class BrailleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrailleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_braille, parent, false)
        return BrailleViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrailleViewHolder, position: Int) {
        val item = images[position]
        Glide.with(holder.itemView.context)
            .load(item.imgPhoto)
            .into(holder.imageView)
    }

    fun updateData(newImages: List<ImageItem>) {
        images = newImages
        notifyDataSetChanged()
    }

    override fun getItemCount() = images.size
}