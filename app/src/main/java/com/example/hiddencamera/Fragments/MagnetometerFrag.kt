package com.example.hiddencamera.Fragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.hiddencamera.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MagnetometerFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class MagnetometerFrag : Fragment(), SensorEventListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var sensorManager: SensorManager? = null
    private var magnetometer: Sensor? = null
    private lateinit var magneticFieldTextView: TextView
    private var isRadarOn = false
    private val REFERENCE_FIELD: Float = 100.0f
    private lateinit var backBtn: ImageView
    private lateinit var radarAnim: LottieAnimationView
    private lateinit var radarBgImageView: ImageView
    //private lateinit var vibrator: Vibrator



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_magnetometer, container, false)
        magneticFieldTextView = v.findViewById(R.id.magneticFieldTextView)
        val enableScanBtn = v.findViewById<Button>(R.id.enableScanBtn)
        radarAnim = v.findViewById(R.id.radarAnim)
        radarBgImageView = v.findViewById(R.id.radarBgImageView)
        backBtn = v.findViewById(R.id.backBtn)
        var isEnableScan = false
        //vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        enableScanBtn.setOnClickListener{

            // Managing radar
            if (isRadarOn) {
                radarAnim.pauseAnimation()
                isRadarOn = false
            } else {
                radarAnim.playAnimation()
                radarAnim.setRepeatCount(LottieDrawable.INFINITE)
                isRadarOn = true
            }

            // Managing Enable button
            if(!isEnableScan){
                enableScanBtn.text = "ON"
                isEnableScan = true
                magnetometer?.let {
                    sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
                }
                enableScanBtn.alpha = 1f

            }
            else{
                enableScanBtn.text = "OFF"
                isEnableScan = false
                sensorManager?.unregisterListener(this)
                magneticFieldTextView.text = "Sensor is Off"
                magneticFieldTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                enableScanBtn.alpha = 0.5f
                val blueDrawable= ContextCompat.getDrawable(requireContext(), R.drawable.radar_bg)
                radarBgImageView.setImageDrawable(blueDrawable)
            }
        }

        backBtn.setOnClickListener{
            requireActivity().onBackPressed()
        }


        return v
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MagnetometerFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MagnetometerFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SensorManager and Magnetometer
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (magnetometer == null) {
            Toast.makeText(requireContext(), "Magnetometer not available on this device", Toast.LENGTH_SHORT).show()
        }
    }

    /*override fun onResume() {
        super.onResume()
        // Register the magnetometer listener
        magnetometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }*/

    override fun onPause() {
        super.onPause()
        // Unregister the listener when the fragment is paused
        sensorManager?.unregisterListener(this)
    }

    private var mediaAndVibratePlayFlag = false

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            // Get the magnetic field strength in microteslas (µT)
            val magneticFieldStrength = Math.sqrt(
                (event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]).toDouble()
            ).toFloat()

            var percentage: Float = (magneticFieldStrength / REFERENCE_FIELD) * 100

            val yellow = ContextCompat.getColor(requireContext(), R.color.yellow)
            val red = ContextCompat.getColor(requireContext(), R.color.red)
            val white = ContextCompat.getColor(requireContext(), R.color.white)
            //val notificationTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            //val mediaPlayer = MediaPlayer.create(requireContext(), notificationTone)

            // Update the UI with the magnetic field strength
            if(magneticFieldStrength<60){
                percentage -= 30
                magneticFieldTextView.setTextColor(white)
                mediaAndVibratePlayFlag = true
            }
            else if(magneticFieldStrength>50 && magneticFieldStrength<80){
                magneticFieldTextView.setTextColor(yellow)
                mediaAndVibratePlayFlag = true
            }
            else if(magneticFieldStrength>80 && magneticFieldStrength<110){
                magneticFieldTextView.setTextColor(red)
                mediaAndVibratePlayFlag = true
            }
            magneticFieldTextView.text = percentage.toInt().toString()+"%"


            // Detect unusual magnetic fields
            if (magneticFieldStrength > 100) { // threshold for unusual field strength
                radarAnim.pauseAnimation()
                val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.radar_bg_red)
                radarBgImageView.setImageDrawable(drawable)
                magneticFieldTextView.text = "Alert: Camera Found!"
                sensorManager?.unregisterListener(this)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Handle if needed later
    }
}