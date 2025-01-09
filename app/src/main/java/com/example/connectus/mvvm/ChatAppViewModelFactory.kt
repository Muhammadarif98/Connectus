package com.example.connectus.mvvm

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatAppViewModelFactory(
    private val lifecycleOwner: LifecycleOwner
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatAppViewModel::class.java)) {
            Log.d("ChatAppViewModelFactory", "Создание экземпляра ChatAppViewModel")
            @Suppress("UNCHECKED_CAST")
            return ChatAppViewModel(lifecycleOwner) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}