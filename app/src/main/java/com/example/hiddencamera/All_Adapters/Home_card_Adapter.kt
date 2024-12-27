package com.example.hiddencamera.All_Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.hiddencamera.All_Models.Home_Card_Model
import com.example.hiddencamera.Fragments.InfraRed_Detection
import com.example.hiddencamera.Fragments.MagnetometerFrag
import com.example.hiddencamera.Fragments.Wifi_Fragment
import com.example.hiddencamera.R

class Home_card_Adapter(
    private val itemsList: ArrayList<Home_Card_Model>, private val context: Context
) : RecyclerView.Adapter<Home_card_Adapter.HomeViewHolder>() {

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.idTV_text)
        val image: ImageView = itemView.findViewById(R.id.idIV_image)
        val cardItem: LinearLayout = itemView.findViewById(R.id.card_item)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.home_page_items, parent, false)
        return HomeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder:Home_card_Adapter.HomeViewHolder, position: Int) {
        holder.title.text = itemsList.get(position).text.toString()
        holder.image.setImageResource(itemsList.get(position).icon)

        if (itemsList[position].text == "History") {
            holder.cardItem.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
            //holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
        } else {
            holder.cardItem.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.main)
            )
        }
        val fragmentTransaction = (holder.itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
        var fragment: Fragment = Wifi_Fragment()
        holder.cardItem.setOnClickListener {
            if(itemsList[position].text == "Wi-Fi Scan") {
                fragment = Wifi_Fragment()
            }
            else if(itemsList[position].text == "Reddot"){
                fragment = InfraRed_Detection()
            }
            else if(itemsList[position].text == "Sensor"){
                fragment = MagnetometerFrag()
            }

            fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}
