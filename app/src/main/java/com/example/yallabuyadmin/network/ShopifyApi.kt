package com.example.yallabuyadmin.network

import com.example.yallabuyadmin.products.model.ProductResponse
import com.example.yallabuyadmin.products.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.*

data class CountResponse(val count: Int)

interface ApiService {
    @GET("products.json")
    suspend fun getAllProducts(): ProductsResponse

    @POST("products.json")
    suspend fun createProduct(@Body product: ProductResponse): ProductResponse

    @PUT("products/{id}.json")
    suspend fun updateProduct(@Path("id") productId: Long, @Body product: ProductResponse): ProductResponse

    @DELETE("products/{id}.json")
    suspend fun deleteProduct(@Path("id") productId: Long): Response<Void>

    @GET("locations/count.json")
    suspend fun getInventoryCount(): CountResponse

    @GET("products/count.json")
    suspend fun getProductsCount(): CountResponse

    @GET("discount_codes/count.json")
    suspend fun getCouponsCount(): CountResponse
}

