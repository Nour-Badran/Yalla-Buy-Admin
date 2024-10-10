package com.example.yallabuyadmin.menu.model

import com.example.yallabuyadmin.network.ApiService

interface IMenuRemoteDataSource {
    suspend fun getInventoryCount(): Int

    suspend fun getProductsCount(): Int

    suspend fun getCouponsCount(): Int
}

class MenuRemoteDataSource(private val api: ApiService) : IMenuRemoteDataSource {
    override suspend fun getInventoryCount(): Int {
        return api.getInventoryCount().count
    }

    override suspend fun getProductsCount(): Int {
        return api.getProductsCount().count
    }

    override suspend fun getCouponsCount(): Int {
        return api.getCouponsCount().count
    }
}
