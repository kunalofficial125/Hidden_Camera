package com.example.hiddencamera.All_Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiddencamera.All_Models.Sliders_Model
import com.example.hiddencamera.R


class Slider_Adapter(private val slider_list: List<Sliders_Model>) : RecyclerView.Adapter<Slider_Adapter.SliderViewHolder>() {

    // ViewHolder class for binding data
    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.txt1)
        val descriptionText: TextView = itemView.findViewById(R.id.txt2)
        val image_slide: ImageView= itemView.findViewById(R.id.image_slide)
        // Bind views here (add imageView or other views if needed)
    }

    // Inflating the layout for each item in RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_items, parent, false)
        return SliderViewHolder(view)
    }

    // Binding data to views in each item of RecyclerView
    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val slider = slider_list[position]
        holder.titleText.text = slider.title
        holder.descriptionText.text = slider.description
        holder.image_slide.setImageResource(slider.slider_image)
        // Example: if your slider model contains an image, set it like:
        // holder.imageView.setImageResource(slider.image)
    }

    // Returning the size of the list
    override fun getItemCount(): Int {
        return slider_list.size
    }
}
