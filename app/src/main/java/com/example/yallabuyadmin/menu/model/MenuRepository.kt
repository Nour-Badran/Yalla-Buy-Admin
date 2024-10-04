package com.example.yallabuyadmin.menu.model

class MenuRepository(private val remoteDataSource: MenuRemoteDataSource) {
    suspend fun getInventoryCount()  = remoteDataSource.getInventoryCount()
    suspend fun getProductsCount() = remoteDataSource.getProductsCount()
    suspend fun getCouponsCount() = remoteDataSource.getCouponsCount()
}
