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
import com.example.brailleapp.databinding.FragmentQuestionsBinding

@Suppress("DEPRECATION")
class QuestionsFragment : Fragment() {

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!

    private val buttonStates = BooleanArray(6) { false } // Status tombol (ON/OFF)
    private var currentScore = 0
    private var currentQuestionIndex = 0

    private val questions = listOf(
        "Huruf A" to listOf(1),
        "Huruf B" to listOf(1, 2),
        "Huruf C" to listOf(1, 4),
        "Huruf D" to listOf(1, 4, 5),
        "Huruf E" to listOf(1, 5),
        "Huruf F" to listOf(1, 2, 4),
        "Huruf G" to listOf(1, 2, 4, 5),
        "Huruf H" to listOf(1, 2, 5),
        "Huruf I" to listOf(2, 4),
        "Huruf J" to listOf(2, 4, 5),
        "Huruf K" to listOf(1, 3),
        "Huruf L" to listOf(1, 2, 3),
        "Huruf M" to listOf(1, 3, 4),
        "Huruf N" to listOf(1, 3, 4, 5),
        "Huruf O" to listOf(1, 3, 5),
        "Huruf P" to listOf(1, 2, 3, 4),
        "Huruf Q" to listOf(1, 2, 3, 4, 5),
        "Huruf R" to listOf(1, 2, 3, 5),
        "Huruf S" to listOf(2, 3, 4),
        "Huruf T" to listOf(2, 3, 4, 5),
        "Huruf U" to listOf(1, 3, 6),
        "Huruf V" to listOf(1, 2, 3, 6),
        "Huruf W" to listOf(2, 4, 5, 6),
        "Huruf X" to listOf(1, 3, 4, 6),
        "Huruf Y" to listOf(1, 3, 4, 5, 6),
        "Huruf Z" to listOf(1, 3, 5, 6),
        "Tanda +" to listOf(2, 3, 5),
        "Tanda -" to listOf(3, 6),
        "Tanda =" to listOf(2, 3, 5, 6),
        "Tanda *" to listOf(3, 5),
        "Tanda /" to listOf(3, 4),
        "Tanda ?" to listOf(2, 6),
        "Tanda !" to listOf(2, 3, 5),
        "Tanda #" to listOf(3, 4, 5, 6),
        "Tanda :" to listOf(2, 5),
        "Tanda ;" to listOf(2, 3)
    )
    private lateinit var randomizedQuestions: List<Pair<String, List<Int>>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        randomizedQuestions = questions.shuffled().take(5)
        currentQuestionIndex = 0
        currentScore = 0

        val buttons = arrayOf(
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6
        )

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
                    val correctAnswer = randomizedQuestions[currentQuestionIndex].second
                    if ((index + 1) in correctAnswer) {
                        vibratePattern(index + 1)
                    }
                }
                true
            }
        }

        binding.btnNext.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                checkAnswer() // Periksa jawaban saat tombol "Berikutnya" ditekan
                currentQuestionIndex++
                if (currentQuestionIndex < randomizedQuestions.size) {
                    loadQuestion()
                } else {
                    binding.tvQuestions.text = "Permainan Selesai! Skor Akhir: $currentScore"
                    binding.btnNext.isEnabled = false
                }
            }
            true
        }

        loadQuestion() // Mulai soal pertama
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadQuestion() {
        val (question, _) = randomizedQuestions[currentQuestionIndex]
        binding.tvQuestions.text = "Soal: $question"
        buttonStates.fill(false) // Reset status tombol
        resetButtonColors() // Reset warna tombol
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun resetButtonColors() {
        val buttons = arrayOf(
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6
        )

        buttons.forEach { button ->
            button.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark, null)) // Default warna OFF
        }
    }

    private fun checkAnswer() {
        val correctAnswer = randomizedQuestions[currentQuestionIndex].second
        val userAnswer = buttonStates.mapIndexed { index, isPressed ->
            if (isPressed) index + 1 else null
        }.filterNotNull()

        if (userAnswer == correctAnswer) {
            currentScore++
            binding.tvScoreGames.text = "Skor: $currentScore"
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
