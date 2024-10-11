package com.example.yallabuyadmin.coupons.model.test

import com.example.yallabuyadmin.coupons.model.CouponsRepository
import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.DiscountCodeRequest
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.fake.FakeCouponsRemoteDataSource
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class CouponsRepositoryTest {

    private val fakeRemoteDataSource = FakeCouponsRemoteDataSource()
    private val couponsRepository = CouponsRepository(fakeRemoteDataSource)

    @Test
    fun `test get price rules`() = runBlocking {
        val priceRules = couponsRepository.getPriceRules().first() // Collecting the flow
        assertEquals(2, priceRules.size) // Ensure two fake price rules are returned
    }

    @Test
    fun `test create price rule`() = runBlocking {
        val newPriceRuleRequest = priceRuleRequest(
            price_rule = PriceRule(title = "New Rule", target_type = "product", value_type = "fixed_amount", value = 15.0, starts_at = "2024-01-01", ends_at = "2024-12-31", target_selection = "specific", allocation_method = "each", customer_selection = "specific", usage_limit = 30)
        )
        val response = couponsRepository.createPriceRule(newPriceRuleRequest)
        assertEquals(3, response.price_rules.size) // Check that the new rule is added
        assertEquals("New Rule", response.price_rules.last().title) // Verify the title of the last added rule
    }

    @Test
    fun `test delete price rule`() = runBlocking {
        val result = couponsRepository.deletePriceRule(1)
        assert(result.isSuccess) // Check if deletion was successful
    }

    @Test
    fun `test update price rule`() = runBlocking {
        val priceRuleUpdateRequest = priceRuleRequest(
            price_rule = PriceRule(title = "Updated Rule", target_type = "product", value_type = "percentage", value = 25.0, starts_at = "2024-01-01", ends_at = "2024-12-31", target_selection = "all", allocation_method = "across", customer_selection = "all", usage_limit = 100)
        )
        val response = couponsRepository.updatePriceRule(1, priceRuleUpdateRequest)
        assertEquals(1L, response.body()?.id) // Check that the ID matches
        assertEquals("Updated Rule", response.body()?.title) // Verify the title is updated
    }

    @Test
    fun `test get discount codes`() = runBlocking {
        val discountCodes = couponsRepository.getDiscountCodes(1).first()
        assertEquals(2, discountCodes.size) // Ensure two fake discount codes are returned
    }
}
