package com.example.yallabuyadmin.menu.Repo

import com.example.yallabuyadmin.menu.FakeRemote.FakeMenuRemoteDataSource
import com.example.yallabuyadmin.menu.model.MenuRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MenuRepositoryTest {

    private lateinit var fakeRemoteDataSource: FakeMenuRemoteDataSource
    private lateinit var menuRepository: MenuRepository

    @Before
    fun setUp() {
        // Initialize the fake remote data source and the repository
        fakeRemoteDataSource = FakeMenuRemoteDataSource()
        menuRepository = MenuRepository(fakeRemoteDataSource)
    }

    @Test
    fun `test getInventoryCount returns correct value`() = runTest {
        // Arrange
        fakeRemoteDataSource.setInventoryCount(10)

        // Act
        val inventoryCount = menuRepository.getInventoryCount()

        // Assert
        assertEquals(10, inventoryCount)
    }

    @Test
    fun `test getProductsCount returns correct value`() = runTest {
        // Arrange
        fakeRemoteDataSource.setProductsCount(5)

        // Act
        val productsCount = menuRepository.getProductsCount()

        // Assert
        assertEquals(5, productsCount)
    }

    @Test
    fun `test getCouponsCount returns correct value`() = runTest {
        // Arrange
        fakeRemoteDataSource.setCouponsCount(20)

        // Act
        val couponsCount = menuRepository.getCouponsCount()

        // Assert
        assertEquals(20, couponsCount)
    }
}
