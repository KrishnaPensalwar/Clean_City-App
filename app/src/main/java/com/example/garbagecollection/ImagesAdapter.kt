package com.example.garbagecollection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImagesAdapter(private val imagesList: List<ImageData>) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageData = imagesList[position]
        holder.bind(imageData)
    }

    override fun getItemCount(): Int = imagesList.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)
        private val locationTextView: TextView = itemView.findViewById(R.id.location_text)

        fun bind(imageData: ImageData) {
            Glide.with(itemView.context).load(imageData.uri).into(imageView)
            descriptionTextView.text = imageData.description
            locationTextView.text = "Lat: ${imageData.latitude}, Lon: ${imageData.longitude}"
        }
    }


}
