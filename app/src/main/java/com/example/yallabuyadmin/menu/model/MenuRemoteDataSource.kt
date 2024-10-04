package com.example.yallabuyadmin.menu.model

import com.example.yallabuyadmin.network.ApiService

class MenuRemoteDataSource(private val api: ApiService) {
    suspend fun getInventoryCount(): Int {
        return api.getInventoryCount().count
    }

    suspend fun getProductsCount(): Int {
        return api.getProductsCount().count
    }

    suspend fun getCouponsCount(): Int {
        return api.getCouponsCount().count
    }
}
