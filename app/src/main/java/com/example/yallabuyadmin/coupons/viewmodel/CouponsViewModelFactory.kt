package com.example.yallabuyadmin.coupons.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yallabuyadmin.coupons.model.CouponsRepository

class CouponsViewModelFactory(private val repository: CouponsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CouponsViewModel::class.java)) {
            return CouponsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
