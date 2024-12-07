package com.example.hiddencamera.All_Adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiddencamera.All_Models.WifiModel
import com.example.hiddencamera.R

class Wifi_Adapter(private val wifiList: List<WifiModel>) :
    RecyclerView.Adapter<Wifi_Adapter.WifiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wifi_list_items, parent, false)
        return WifiViewHolder(view)
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        val wifi = wifiList[position]


        holder.wifiName.text = wifi.ssid
        holder.wifiStrength.text = "${wifi.signalStrength}%"

        val safeText = "Safe";
        val unsafeText = "Not Safe"
        val suspiciousText = "Suspicious"

        // Check if network uses secure encryption (WPA2, WPA3) or is insecure (WEP, open)
        if (wifi.safetyStatus.contains("WPA2") || wifi.safetyStatus.contains("WPA3")) {
            holder.wifiSafety.text = safeText
            holder.wifiSafety.setTextColor(Color.GREEN)
            Log.d("WifiSecurity", "${wifi.ssid}: Secure (WPA2/WPA3)")
        }
        else if (wifi.safetyStatus.contains("WEP") || wifi.safetyStatus.contains("WPS")) {
            holder.wifiSafety.text = unsafeText
            holder.wifiSafety.setTextColor(Color.RED)
            Log.d("WifiSecurity", "${wifi.ssid}: Insecure (WEP or WPS)")
        }
        else {
            holder.wifiSafety.text = suspiciousText
            holder.wifiSafety.setTextColor(Color.YELLOW)
            Log.d("WifiSecurity", "${wifi.ssid}: Open network")
        }

    }

    override fun getItemCount(): Int = wifiList.size

    inner class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wifiName: TextView = itemView.findViewById(R.id.wifi_name)
        val wifiStrength: TextView = itemView.findViewById(R.id.wifi_strength)
        val wifiSafety: TextView = itemView.findViewById(R.id.wifi_safety)
    }
}
