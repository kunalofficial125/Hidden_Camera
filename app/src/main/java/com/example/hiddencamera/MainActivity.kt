package com.example.hiddencamera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.hiddencamera.Fragments.Home_Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA), 1098)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            //return
        }

//        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//
//        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//
//        if(!isLocationEnabled){
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(intent)
//        }

        if (savedInstanceState == null) {
            loadFragment(Home_Fragment())
        }
    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
