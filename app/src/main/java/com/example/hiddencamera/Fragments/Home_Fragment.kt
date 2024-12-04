package com.example.hiddencamera.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiddencamera.All_Adapters.Home_card_Adapter
import com.example.hiddencamera.All_Models.Home_Card_Model
import com.example.hiddencamera.R

class Home_Fragment : Fragment() {

    // Declare variables at the class level
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeCardAdapter:Home_card_Adapter
    private lateinit var itemList: ArrayList<Home_Card_Model>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.home_items_recy)

        // Initialize the item list
        itemList = ArrayList()

        // Add data to the list
        itemList.add(Home_Card_Model("Wi-Fi Scan", R.drawable.wifi_icon))
        itemList.add(Home_Card_Model("Reddot", R.drawable.redot))
        itemList.add(Home_Card_Model("Sensor", R.drawable.sensor))
        itemList.add(Home_Card_Model("History", R.drawable.history))

        // Initialize the adapter
        homeCardAdapter = Home_card_Adapter(itemList)

        // Set the layout manager and adapter to the RecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = homeCardAdapter

        return view
    }
}
