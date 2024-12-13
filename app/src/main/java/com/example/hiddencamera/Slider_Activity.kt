package com.example.hiddencamera

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.hiddencamera.All_Adapters.Slider_Adapter
import com.example.hiddencamera.All_Models.Sliders_Model

class Slider_Activity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: Slider_Adapter
    private lateinit var btn_cont: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge content
        setContentView(R.layout.activity_slider)

        // Initialize all views and components
        initViews()

        // Define the slider list
        val sliderList = listOf(
            Sliders_Model(
                "Camera Detector",
                "Search for possible suspicious devices under the same Wi-Fi to prevent your life from being monitored",
                R.drawable.slider_1
            ),
            Sliders_Model(
                "Red Dot Detection",
                "Our app’s specialized feature detects red dot cameras,\n even in low-light scenarios, ensuring your protection in any environment.",
                R.drawable.slider2
            )
        )

        // Set the adapter with the slider data
        adapter = Slider_Adapter(sliderList)
        viewPager2.adapter = adapter

        // Apply system bar insets to adjust for edge-to-edge content
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle button click
        btn_cont.setOnClickListener {
            val currentItem = viewPager2.currentItem
            if (currentItem < sliderList.size - 1) {
                // Move to the next slide
                viewPager2.currentItem = currentItem + 1
            } else {
                // If it's the last slide, move to MainActivity
                val intent = Intent(this@Slider_Activity, MainActivity::class.java)

                //Login Check
                val sharedPreferences: SharedPreferences = getSharedPreferences("LoginCheck", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean("isLogin",true)
                editor.apply()

                startActivity(intent)
                finish()
            }
        }
    }

    private fun initViews() {
        // Initialize all views here
        viewPager2 = findViewById(R.id.slider_pager)
        btn_cont = findViewById(R.id.continue_button)
    }
}
