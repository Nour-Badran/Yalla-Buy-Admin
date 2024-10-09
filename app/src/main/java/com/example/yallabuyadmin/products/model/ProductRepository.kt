package com.example.yallabuyadmin.products.model

import android.util.Log
import com.example.yallabuyadmin.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

class ProductRepository(private val api: ApiService) {

    // Fetch all products
    suspend fun getAllProducts(): Flow<List<Product>> = flow {
        emit(api.getAllProducts().products)
    }

    // Create a new product
    suspend fun createProduct(product: Product) {
        api.createProduct(ProductResponse(product))
    }
    suspend fun getVariants(productId: Long): Flow<List<Variant>> = flow {
        emit(api.getVariants(productId).variants)
    }
    // Update a product
    suspend fun updateProduct(product: Product) {
        api.updateProduct(product.id ?: 0, ProductResponse(product))
    }

    // Delete a product
    suspend fun deleteProduct(productId: Long) {
        api.deleteProduct(productId)
    }

    suspend fun updateVariant(variant: Variant) {
        Log.d("Variant",variant.toString())
        // Wrap the variant in VariantRequest
        val variantRequest = VariantRequest(variant)

        // Send the request to Shopify
        api.updateVariant(variant.id!!, variantRequest)
    }
    suspend fun createVariant(productId: Long,variant: Variant)
    {
        val variantRequest = VariantRequest(variant)
        api.createVariant(productId,variantRequest)
    }
    suspend fun deleteVariant(productId: Long,variantId: Long){
        api.deleteVariant(productId,variantId = variantId)
    }
}
