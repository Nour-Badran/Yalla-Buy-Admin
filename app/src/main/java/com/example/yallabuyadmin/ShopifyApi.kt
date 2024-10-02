package com.example.yallabuyadmin

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("products.json")
    suspend fun getAllProducts(): ProductsResponse

    @POST("products.json")
    suspend fun createProduct(@Body product: ProductResponse): ProductResponse

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") productId: Long, @Body product: ProductResponse): ProductResponse

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Long): Response<Void>
}

