package com.example.brailleapp.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.brailleapp.databinding.FragmentListBrailleBinding
import com.example.brailleapp.retrofit.ApiConfig

class ListBrailleFragment : Fragment() {
    private var _binding: FragmentListBrailleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListBrailleViewModel by viewModels {
        ListBrailleViewModelFactory(ListBrailleRepository(ApiConfig.apiImageService))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBrailleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BrailleAdapter(emptyList())
        binding.rvListBraille.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvListBraille.adapter = adapter

        viewModel.getImages().observe(viewLifecycleOwner) { images ->
            adapter.updateData(images)
        }
    }

}