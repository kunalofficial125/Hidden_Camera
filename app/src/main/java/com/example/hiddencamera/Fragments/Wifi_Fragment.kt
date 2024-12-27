package com.example.hiddencamera.Fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
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
    private lateinit var results: List<ScanResult>
    private lateinit var deviceStatusTextView: TextView
    private lateinit var backBtn: ImageView
    private lateinit var deviceStatusImageView: ImageView
    private lateinit var radarAnim: LottieAnimationView
    private lateinit var radarBgImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_wifi_, container, false)

        // Initialize views
        startScanButton = binding.findViewById(R.id.scan_btn)
        //stopScanButton = binding.findViewById(R.id.Stop_scan)
        recyclerWifi = binding.findViewById(R.id.wifi_list)
        radarAnim = binding.findViewById(R.id.radarAnim)
        backBtn = binding.findViewById(R.id.backBtn)
        radarBgImageView = binding.findViewById(R.id.radarBgImageView)
        deviceStatusTextView = binding.findViewById(R.id.deviceStatusTextView)
        deviceStatusImageView = binding.findViewById(R.id.deviceStatusImageView)

        //Ask For Location Service
        redirectToLocationPermission()

        // Initialize the Wi-Fi list and adapter
        wifiList = mutableListOf()
        wifiAdapter = Wifi_Adapter(wifiList, this)

        // Set up RecyclerView
        recyclerWifi.layoutManager =  LinearLayoutManager(requireContext())
        recyclerWifi.adapter = wifiAdapter

        // Get WifiManager
        wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Set up button click listeners
        startScanButton.setOnClickListener {
            if(startScanButton.text == "Scan"){
                startScanButton.text = "Stop"
                startWifiScan()
            }
            else{
                startScanButton.text = "Scan"
                stopWifiScan()
            }
        }

        //stopScanButton.setOnClickListener { stopWifiScan() }

        //stopScanButton.isEnabled = false // Initially, stop button should be disabled

        backBtn.setOnClickListener{
            requireActivity().onBackPressed()
        }

        return binding
    }

    private fun startWifiScan() {
        //Check For Location Services

        // Check permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            //return
        }

        //start radar
        radarAnim.setRepeatCount(LottieDrawable.INFINITE)
        radarAnim.playAnimation()

        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true // Enable Wi-Fi if disabled
        }

        // Register BroadcastReceiver
        if (wifiScanReceiver == null) {
            wifiScanReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    // Retrieve scan results
                    results = wifiManager.scanResults
                    wifiList.clear() // Clear the old list

                    Log.d("WifiResults","Fetched:${results.size}")

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
                                safetyStatus = capabilities
                            )
                        )

                    }

                    var statusFlag = true

                    for(wifi in results){

                        if(wifi.capabilities.contains("WPA2") || wifi.capabilities.contains("WPA3") ||
                            wifi.capabilities.contains("WPA")){

                            statusFlag = true
                        }
                        else if(wifi.capabilities.contains("WEP") || wifi.capabilities.contains("WPS")){
                            deviceStatusTextView.visibility = View.VISIBLE
                            deviceStatusImageView.visibility = View.VISIBLE
                            deviceStatusTextView.text = "Device Found"
                            statusFlag = false

                            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.found)
                            deviceStatusImageView.setImageDrawable(drawable)
                            break
                        }
                    }

                    if(statusFlag){
                        deviceStatusTextView.visibility = View.VISIBLE
                        deviceStatusImageView.visibility = View.VISIBLE
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

        //startScanButton.isEnabled = false
        //stopScanButton.isEnabled = true
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

        //startScanButton.isEnabled = true
        //stopScanButton.isEnabled = false
        deviceStatusImageView.visibility = View.GONE
        deviceStatusTextView.visibility = View.GONE
        radarAnim.pauseAnimation()

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

    private fun redirectToLocationPermission(){
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager


        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(!isLocationEnabled){
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }


    public fun changeRadarColor(){
        val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.radar_bg_red)
        radarBgImageView.setImageDrawable(drawable)
        radarAnim.pauseAnimation()
        deviceStatusTextView.text = "Device Found"
        val drawable2 = ContextCompat.getDrawable(requireContext(), R.drawable.found)
        deviceStatusImageView.setImageDrawable(drawable2)

    }

}
