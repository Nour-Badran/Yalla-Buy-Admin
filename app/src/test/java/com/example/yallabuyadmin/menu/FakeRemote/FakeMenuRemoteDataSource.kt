package com.example.yallabuyadmin.menu.FakeRemote

import com.example.yallabuyadmin.menu.model.IMenuRemoteDataSource

class FakeMenuRemoteDataSource : IMenuRemoteDataSource {
    private var inventoryCount = 0
    private var productsCount = 0
    private var couponsCount = 0

    fun setInventoryCount(count: Int) {
        inventoryCount = count
    }

    fun setProductsCount(count: Int) {
        productsCount = count
    }

    fun setCouponsCount(count: Int) {
        couponsCount = count
    }

    override suspend fun getInventoryCount(): Int {
        return inventoryCount
    }

    override suspend fun getProductsCount(): Int {
        return productsCount
    }

    override suspend fun getCouponsCount(): Int {
        return couponsCount
    }
}