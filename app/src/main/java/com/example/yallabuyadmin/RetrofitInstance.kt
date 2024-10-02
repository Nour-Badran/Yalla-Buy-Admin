package com.example.yallabuyadmin

import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitInstance {

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", Credentials.basic("6d9bf027d5ce002ce4f2697d43adccdf", "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"))
                .build()
            chain.proceed(request)
        }
        .build()


    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://android-sv24-r3team4.myshopify.com/admin/api/2024-07/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }


    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
