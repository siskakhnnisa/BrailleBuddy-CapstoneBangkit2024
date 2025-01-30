package com.example.brailleapp.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brailleapp.R
import com.example.brailleapp.databinding.FragmentGameBinding
import com.example.brailleapp.ui.game.QuestionActivity
import com.example.brailleapp.ui.game.TryBrailleActivity

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btYes.setOnClickListener {
            // Start QuestionActivity
            val intent = Intent(activity, QuestionActivity::class.java)
            startActivity(intent)
        }

        binding.btNo.setOnClickListener {
            // Start TryBrailleActivity
            val intent = Intent(activity, TryBrailleActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
