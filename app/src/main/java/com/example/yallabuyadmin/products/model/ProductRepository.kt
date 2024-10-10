package com.example.yallabuyadmin.products.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface IProductRepository {
    // Fetch all products
    suspend fun getAllProducts(): Flow<List<Product>>

    // Create a new product
    suspend fun createProduct(product: Product)

    // Get product variants
    suspend fun getVariants(productId: Long): Flow<List<Variant>>

    // Update a product
    suspend fun updateProduct(product: Product)

    // Delete a product
    suspend fun deleteProduct(productId: Long)

    // Update a variant
    suspend fun updateVariant(variant: Variant)

    // Create a variant
    suspend fun createVariant(productId: Long, variant: Variant)

    // Delete a variant
    suspend fun deleteVariant(productId: Long, variantId: Long)
}

class ProductRepository(private val remoteDataSource: IProductRemoteDataSource) :
    IProductRepository {

    // Fetch all products
    override suspend fun getAllProducts(): Flow<List<Product>> = flow {
        emit(remoteDataSource.getAllProducts())
    }

    // Create a new product
    override suspend fun createProduct(product: Product) {
        remoteDataSource.createProduct(product)
    }

    // Get product variants
    override suspend fun getVariants(productId: Long): Flow<List<Variant>> = flow {
        emit(remoteDataSource.getVariants(productId))
    }

    // Update a product
    override suspend fun updateProduct(product: Product) {
        remoteDataSource.updateProduct(product)
    }

    // Delete a product
    override suspend fun deleteProduct(productId: Long) {
        remoteDataSource.deleteProduct(productId)
    }

    // Update a variant
    override suspend fun updateVariant(variant: Variant) {
        remoteDataSource.updateVariant(variant)
    }

    // Create a variant
    override suspend fun createVariant(productId: Long, variant: Variant) {
        remoteDataSource.createVariant(productId, variant)
    }

    // Delete a variant
    override suspend fun deleteVariant(productId: Long, variantId: Long) {
        remoteDataSource.deleteVariant(productId, variantId)
    }
}
