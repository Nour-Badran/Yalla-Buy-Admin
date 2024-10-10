package com.example.yallabuyadmin.products.model

import com.example.yallabuyadmin.network.ApiService
import retrofit2.Response

interface IProductRemoteDataSource {
    suspend fun getAllProducts(): List<Product>

    suspend fun createProduct(product: Product): ProductResponse

    suspend fun getVariants(productId: Long): List<Variant>

    suspend fun updateProduct(product: Product): ProductResponse

    suspend fun deleteProduct(productId: Long): Response<Void>

    suspend fun updateVariant(variant: Variant)

    suspend fun createVariant(productId: Long, variant: Variant)

    suspend fun deleteVariant(productId: Long, variantId: Long): Response<Void>
}

class ProductRemoteDataSource(private val apiService: ApiService) : IProductRemoteDataSource {

    override suspend fun getAllProducts() = apiService.getAllProducts().products

    override suspend fun createProduct(product: Product) = apiService.createProduct(ProductResponse(product))

    override suspend fun getVariants(productId: Long) = apiService.getVariants(productId).variants

    override suspend fun updateProduct(product: Product) = apiService.updateProduct(product.id ?: 0, ProductResponse(product))

    override suspend fun deleteProduct(productId: Long) = apiService.deleteProduct(productId)

    override suspend fun updateVariant(variant: Variant) {
        val variantRequest = VariantRequest(variant)
        apiService.updateVariant(variant.id!!, variantRequest)
    }

    override suspend fun createVariant(productId: Long, variant: Variant) {
        val variantRequest = VariantRequest(variant)
        apiService.createVariant(productId, variantRequest)
    }

    override suspend fun deleteVariant(productId: Long, variantId: Long) = apiService.deleteVariant(productId, variantId)
}
