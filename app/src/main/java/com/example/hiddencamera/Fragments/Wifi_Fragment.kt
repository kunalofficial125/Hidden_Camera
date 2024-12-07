package com.example.hiddencamera.Fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiddencamera.All_Adapters.Wifi_Adapter
import com.example.hiddencamera.All_Models.WifiModel
import com.example.hiddencamera.R

class Wifi_Fragment : Fragment() {

    private lateinit var wifiManager: WifiManager
    private lateinit var startScanButton: Button
    private lateinit var stopScanButton: Button
    private lateinit var wifiAdapter: Wifi_Adapter
    private lateinit var recyclerWifi: RecyclerView
    private lateinit var wifiList: MutableList<WifiModel> // List to hold Wi-Fi data
    private var wifiScanReceiver: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_wifi_, container, false)

        // Initialize views
        startScanButton = binding.findViewById(R.id.scan_btn)
        stopScanButton = binding.findViewById(R.id.Stop_scan)
        recyclerWifi = binding.findViewById(R.id.wifi_list)

        // Initialize the Wi-Fi list and adapter
        wifiList = mutableListOf()
        wifiAdapter = Wifi_Adapter(wifiList)

        // Set up RecyclerView
        recyclerWifi.layoutManager = LinearLayoutManager(context)
        recyclerWifi.adapter = wifiAdapter

        // Get WifiManager
        wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Set up button click listeners
        startScanButton.setOnClickListener { startWifiScan() }
        stopScanButton.setOnClickListener { stopWifiScan() }

        stopScanButton.isEnabled = false // Initially, stop button should be disabled

        return binding
    }

    private fun startWifiScan() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            //return
        }

        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true // Enable Wi-Fi if disabled
        }

        // Register BroadcastReceiver
        if (wifiScanReceiver == null) {
            wifiScanReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    // Retrieve scan results
                    val results: List<ScanResult> = wifiManager.scanResults
                    wifiList.clear() // Clear the old list

                    Log.d("WifiResults","Fetched")

                    // Add new results to the list
                    results.forEach { result ->
                        val ssid = result.SSID // Name of the Wi-Fi network
                        val signalStrength = result.level // Signal strength
                        val capabilities = result.capabilities // Security and pattern details

                        // Add Wi-Fi data to the list
                        wifiList.add(
                            WifiModel(
                                ssid = ssid,
                                signalStrength = signalStrength.toString(),
                                safetyStatus= capabilities
                            )
                        )
                    }

                    // Notify adapter about the dataset change
                    wifiAdapter.notifyDataSetChanged()
                }
            }
        }

        requireContext().registerReceiver(
            wifiScanReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )

        wifiManager.startScan()

        startScanButton.isEnabled = false
        stopScanButton.isEnabled = true
    }

    private fun stopWifiScan() {
        wifiScanReceiver?.let {
            try {
                requireContext().unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                // Receiver may not be registered; ignore safely
            }
            wifiScanReceiver = null
        }

        startScanButton.isEnabled = true
        stopScanButton.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        wifiScanReceiver?.let {
            try {
                requireContext().unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                // Ignore if already unregistered
            }
            wifiScanReceiver = null
        }
    }
}
