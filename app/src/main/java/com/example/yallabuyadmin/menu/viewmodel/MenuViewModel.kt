package com.example.yallabuyadmin.menu.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.yallabuyadmin.menu.model.MenuRepository

class MenuViewModel(private val repository: MenuRepository) : ViewModel() {

    private val _inventoryCount = MutableStateFlow(0)
    val inventoryCount: StateFlow<Int> = _inventoryCount

    private val _productsCount = MutableStateFlow(0)
    val productsCount: StateFlow<Int> = _productsCount

    private val _couponsCount = MutableStateFlow(0)
    val couponsCount: StateFlow<Int> = _couponsCount

    init {
        loadCounts()
    }

    fun loadCounts() {
        viewModelScope.launch {
            try {
                _inventoryCount.value = repository.getInventoryCount()
                _productsCount.value = repository.getProductsCount()
                _couponsCount.value = repository.getCouponsCount()
            } catch (e: Exception) {
                Log.d("Error:", e.toString())
                // Handle error here
            }
        }
    }
}
