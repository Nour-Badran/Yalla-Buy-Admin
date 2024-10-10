package com.example.yallabuyadmin.menu.model

interface IMenuRepository {
    suspend fun getInventoryCount(): Int

    suspend fun getProductsCount(): Int

    suspend fun getCouponsCount(): Int
}

class MenuRepository(private val remoteDataSource: IMenuRemoteDataSource) : IMenuRepository {
    override suspend fun getInventoryCount()  = remoteDataSource.getInventoryCount()
    override suspend fun getProductsCount() = remoteDataSource.getProductsCount()
    override suspend fun getCouponsCount() = remoteDataSource.getCouponsCount()
}
