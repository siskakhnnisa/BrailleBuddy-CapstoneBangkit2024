package com.example.brailleapp.ui.game

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.brailleapp.databinding.ActivityTryBrailleBinding

@Suppress("DEPRECATION")
class TryBrailleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTryBrailleBinding
    private val buttonStates = BooleanArray(6) { false } // Status tombol (ON/OFF)

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTryBrailleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnBack = binding.btnBack
        val buttons = arrayOf(
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6
        )

        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Set listener untuk masing-masing tombol
        buttons.forEachIndexed { index, button ->
            button.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    // Toggle status tombol
                    buttonStates[index] = !buttonStates[index]

                    // Ubah warna tombol berdasarkan status
                    if (buttonStates[index]) {
                        button.setBackgroundColor(resources.getColor(android.R.color.holo_blue_dark, null)) // ON
                    } else {
                        button.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null)) // OFF
                    }

                    // Getarkan perangkat
                    vibratePattern(index + 1)
                }
                true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun vibratePattern(repeatCount: Int) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VibratorManager::class.java)
            vibratorManager?.defaultVibrator
        } else {
            getSystemService(Vibrator::class.java)
        }

        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationPattern = mutableListOf<Long>()
                for (i in 1..repeatCount) {
                    vibrationPattern.add(0)
                    vibrationPattern.add(100)
                    vibrationPattern.add(100)
                }

                it.vibrate(
                    VibrationEffect.createWaveform(
                        vibrationPattern.toLongArray(),
                        -1
                    )
                )
            } else {
                for (i in 1..repeatCount) {
                    it.vibrate(200)
                    Thread.sleep(200)
                }
            }
        }
    }
}
