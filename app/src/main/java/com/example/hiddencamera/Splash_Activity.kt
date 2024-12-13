package com.example.hiddencamera

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Splash_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Adjust padding for edge-to-edge content
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Add a delay of 3 seconds and then navigate to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({

            val sharedPreferences: SharedPreferences = getSharedPreferences("LoginCheck", MODE_PRIVATE)
            val isLogin = sharedPreferences.getBoolean("isLogin",false)
            val intent: Intent

            if(isLogin){
                intent = Intent(this@Splash_Activity, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                intent = Intent(this@Splash_Activity, Slider_Activity::class.java)
                startActivity(intent)
            }

            finish()
        }, 3000)
    }
}
