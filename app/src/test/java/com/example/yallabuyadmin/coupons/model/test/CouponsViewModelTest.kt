package com.example.yallabuyadmin.coupons.model.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.fake.FakeCouponsRepository
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import com.example.yallabuyadmin.coupons.viewmodel.CouponsViewModel
import com.example.yallabuyadmin.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CouponsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CouponsViewModel
    private lateinit var fakeRepository: FakeCouponsRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeCouponsRepository()
        viewModel = CouponsViewModel(fakeRepository)
    }

    @Test
    fun `test fetch price rules`() = runTest {
        viewModel.fetchPriceRules()
        advanceUntilIdle()  // Wait for coroutines to finish

        val state = viewModel.priceRules.value
        assertEquals(true, state is ApiState.Success)
        val priceRules = (state as ApiState.Success).data
        assertEquals(2, priceRules.size) // Ensure two fake price rules are returned
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher after the test
        Dispatchers.resetMain()
        testDispatcher.cancel() // Cancel the dispatcher to clean up
    }
}
