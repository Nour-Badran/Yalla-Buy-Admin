package com.example.yallabuyadmin.menu.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.yallabuyadmin.menu.FakeRepo.FakeMenuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeRepository: FakeMenuRepository
    private lateinit var viewModel: MenuViewModel

    // Use StandardTestDispatcher instead of UnconfinedTestDispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set the Main dispatcher to a TestDispatcher before the test
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeMenuRepository()
        viewModel = MenuViewModel(fakeRepository)
    }

    @Test
    fun `test loadCounts with default values`() = runTest {
        // Act
        viewModel.loadCounts()

        // Assert initial values
        assertEquals(0, viewModel.inventoryCount.value)
        assertEquals(0, viewModel.productsCount.value)
        assertEquals(0, viewModel.couponsCount.value)
    }

    @Test
    fun `test loadCounts with specific values`() = runTest {
        // Arrange
        fakeRepository.setInventoryCount(5)
        fakeRepository.setProductsCount(10)
        fakeRepository.setCouponsCount(15)

        // Act
        viewModel.loadCounts()
        advanceUntilIdle()  // Wait for coroutines to finish

        // Assert values after loading
        assertEquals(5, viewModel.inventoryCount.value)
        assertEquals(10, viewModel.productsCount.value)
        assertEquals(15, viewModel.couponsCount.value)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher after the test
        Dispatchers.resetMain()
        testDispatcher.cancel() // Cancel the dispatcher to clean up
    }
}
