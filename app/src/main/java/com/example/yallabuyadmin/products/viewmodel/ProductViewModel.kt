package com.example.yallabuyadmin.products.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuyadmin.network.ApiState
import com.example.yallabuyadmin.products.model.IProductRepository
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.model.ProductRepository
import com.example.yallabuyadmin.products.model.Variant
import com.example.yallabuyadmin.products.model.VariantRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

class ProductViewModel(private val repository: IProductRepository) : ViewModel() {

    private val _apiState = MutableStateFlow<ApiState<List<Product>>>(ApiState.Loading)
    val apiState: StateFlow<ApiState<List<Product>>> get() = _apiState

    private val _variants= MutableStateFlow<ApiState<List<Variant>>>(ApiState.Loading)
    val variants: StateFlow<ApiState<List<Variant>>> get() = _variants

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _isUpdating = mutableStateOf(false)
    val isUpdating: State<Boolean> get() = _isUpdating

    private val _deletingProducts = mutableStateMapOf<Long, Boolean>() // Map to track deleting state by product ID
    val deletingProducts: Map<Long, Boolean> get() = _deletingProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> get() = _successMessage

    fun clearSuccess() {
        _successMessage.value = null
    }
    // Clear the error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Fetch all products
    fun getAllProducts() {
        viewModelScope.launch {
            repository.getAllProducts()
                .onStart {
                    _apiState.value = ApiState.Loading // Set loading state
                }.catch { e ->
                    _apiState.value = ApiState.Error(e.message ?: "Unknown error")
                    _errorMessage.value = e.message // Set error message
                }
                .collect { productList ->
                    _apiState.value = ApiState.Success(productList) // Set success state with data
                }
        }
    }

    fun getVariants(productId: Long){
        viewModelScope.launch {
            repository.getVariants(productId)
                .onStart {
                    _variants.value = ApiState.Loading
                }.catch { e->
                    _variants.value = ApiState.Error(e.message ?: "Unknown error")
                    _errorMessage.value = e.message
                }.collect{ variants ->
                    _variants.value = ApiState.Success(variants)
                }
        }
    }

    fun createProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true // Set loading state to true
            try {
                repository.createProduct(product)
                _successMessage.value = "Product created successfully!" // Set success message
                getAllProducts() // Refresh product list
                clearError() // Clear error after successful creation
            } catch (e: Exception) {
                _errorMessage.value = e.message // Set error message
            } finally {
                _isLoading.value = false // Set loading state to false
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _isUpdating.value = true
            clearSuccess() // Clear previous success message
            try {
                repository.updateProduct(product)
                _successMessage.value = "Product updated successfully!" // Set success message
                getAllProducts() // Refresh product list
                clearError() // Clear error after successful update
            } catch (e: Exception) {
                _errorMessage.value = e.message // Set error message
            } finally {
                _isUpdating.value = false // Set updating state to false
            }
        }
    }
    fun createVariant(productId: Long,variant: Variant)
    {
        viewModelScope.launch {
            _isLoading.value = true // Set loading state to true
            try {
                repository.createVariant(productId,variant)
                _successMessage.value = "Variant created successfully!" // Set success message
                clearError() // Clear error after successful creation
            } catch (e: Exception) {
                _errorMessage.value = e.message // Set error message
            } finally {
                _isLoading.value = false // Set loading state to false
            }
        }
    }
    fun updateVariant(variant: Variant){
        viewModelScope.launch {
            _isUpdating.value = true
            clearSuccess() // Clear previous success message
            try{
                repository.updateVariant(variant)
                _successMessage.value = "Variant updated successfully!" // Set success message
                getAllProducts() // Refresh product list
                clearError() // Clear error after successful update
            } catch (e: Exception) {
                _errorMessage.value = e.message // Set error message
            } finally {
                _isUpdating.value = false // Set updating state to false
            }
        }
    }
    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            _deletingProducts[productId] = true // Set the specific product's deleting state to true
            try {
                repository.deleteProduct(productId)
                _successMessage.value = "Product deleted successfully!" // Set success message
                getAllProducts() // Refresh product list
                clearError() // Clear error after successful deletion
            } catch (e: Exception) {
                _errorMessage.value = e.message // Set error message
            } finally {
                _deletingProducts.remove(productId) // Remove the product from the deleting state map
            }
        }
    }
    fun deleteVariant(productId: Long,variantId: Long){
        viewModelScope.launch {
            try {
                repository.deleteVariant(productId,variantId)
                _successMessage.value = "Variant deleted successfully!" // Set success message
                getAllProducts() // Refresh product list
                clearError() // Clear error after successful deletion
            } catch (e: Exception) {
                _errorMessage.value = e.message // Set error message
            }
        }
    }
}
