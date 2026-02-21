package com.trevisol.photos.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.trevisol.photos.domain.entities.Photo

class PhotosAdapter(
   private val activityContext: Context,
   private val photos: List<Photo>
): ArrayAdapter<Photo>(activityContext, android.R.layout.simple_list_item_1, photos) {
    private data class ProductViewHolder(val photoTitleTextView: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val productView = convertView ?: LayoutInflater.from(activityContext)
            .inflate(android.R.layout.simple_list_item_1, parent, false).apply {
                tag = ProductViewHolder(findViewById(android.R.id.text1))
            }

        (productView.tag as ProductViewHolder).photoTitleTextView.text = photos[position].title

        return productView
    }
}
