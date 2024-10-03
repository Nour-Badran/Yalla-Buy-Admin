package com.example.yallabuyadmin.products.model

import com.example.yallabuyadmin.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepository(private val api: ApiService) {

    // Fetch all products
    suspend fun getAllProducts(): Flow<List<Product>> = flow {
        emit(api.getAllProducts().products)
    }

    // Create a new product
    suspend fun createProduct(product: Product) {
        api.createProduct(ProductResponse(product))
    }

    // Update a product
    suspend fun updateProduct(product: Product) {
        api.updateProduct(product.id ?: 0, ProductResponse(product))
    }

    // Delete a product
    suspend fun deleteProduct(productId: Long) {
        api.deleteProduct(productId)
    }
}
