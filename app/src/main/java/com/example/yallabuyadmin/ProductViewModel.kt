package com.example.yallabuyadmin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel : ViewModel() {

    private val api = RetrofitInstance.api

    // Fetch all products
    fun getAllProducts(onSuccess: (List<Product>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.getAllProducts()
                Log.i("Test",response.toString())
                onSuccess(response.products)
            } catch (e: Exception) {
                Log.d("Test",e.toString())
                onError(e.message ?: "Unknown error")
            }
        }
    }

    // Create a new product
    fun createProduct(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                api.createProduct(ProductResponse(product))
                Log.d("CreateProduct", "Product data: $product")
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun updateProduct(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                api.updateProduct(product.id ?: 0, ProductResponse(product))
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteProduct(productId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                api.deleteProduct(productId)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

}

