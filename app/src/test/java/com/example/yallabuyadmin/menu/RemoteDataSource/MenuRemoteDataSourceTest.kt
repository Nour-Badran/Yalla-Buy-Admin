package com.example.yallabuyadmin.menu.RemoteDataSource

import com.example.yallabuyadmin.coupons.model.fake.FakeApiService
import com.example.yallabuyadmin.menu.model.MenuRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MenuRemoteDataSourceTest {

    private lateinit var fakeApiService: FakeApiService
    private lateinit var menuRemoteDataSource: MenuRemoteDataSource

    @Before
    fun setUp() {
        fakeApiService = FakeApiService()
        menuRemoteDataSource = MenuRemoteDataSource(fakeApiService)
    }

    @Test
    fun `test getInventoryCount returns correct count`() = runTest {
        // Arrange
        val expectedInventoryCount = 10
        fakeApiService.setInventoryCount(expectedInventoryCount)

        // Act
        val actualInventoryCount = menuRemoteDataSource.getInventoryCount()

        // Assert
        assertEquals(expectedInventoryCount, actualInventoryCount)
    }

    @Test
    fun `test getProductsCount returns correct count`() = runTest {
        // Arrange
        val expectedProductsCount = 20
        fakeApiService.setProductsCount(expectedProductsCount)

        // Act
        val actualProductsCount = menuRemoteDataSource.getProductsCount()

        // Assert
        assertEquals(expectedProductsCount, actualProductsCount)
    }

    @Test
    fun `test getCouponsCount returns correct count`() = runTest {
        // Arrange
        val expectedCouponsCount = 5
        fakeApiService.setCouponsCount(expectedCouponsCount)

        // Act
        val actualCouponsCount = menuRemoteDataSource.getCouponsCount()

        // Assert
        assertEquals(expectedCouponsCount, actualCouponsCount)
    }
}
