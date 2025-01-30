package com.example.brailleapp.ui.game

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.brailleapp.databinding.FragmentTryBrailleBinding

@Suppress("DEPRECATION")
class TryBrailleFragment : Fragment() {

    private var _binding: FragmentTryBrailleBinding? = null
    private val binding get() = _binding!!

    private val buttonStates = BooleanArray(6) { false } // Status tombol (ON/OFF)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTryBrailleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            requireActivity().onBackPressed()
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
        val context = requireContext()
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            vibratorManager?.defaultVibrator
        } else {
            context.getSystemService(Vibrator::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
