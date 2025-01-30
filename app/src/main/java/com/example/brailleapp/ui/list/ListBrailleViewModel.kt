package com.example.brailleapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData

class ListBrailleViewModel(private val repository: ListBrailleRepository): ViewModel() {
    fun getImages() = liveData {
        try {
            val response = repository.getImages()
            emit(response.dataImage)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}

class ListBrailleViewModelFactory(private val repository: ListBrailleRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListBrailleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListBrailleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}