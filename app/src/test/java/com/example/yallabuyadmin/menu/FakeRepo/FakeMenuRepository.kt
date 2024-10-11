package com.example.yallabuyadmin.menu.FakeRepo

import com.example.yallabuyadmin.menu.model.IMenuRepository

class FakeMenuRepository : IMenuRepository {

    private var inventoryCount: Int = 0
    private var productsCount: Int = 0
    private var couponsCount: Int = 0

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
