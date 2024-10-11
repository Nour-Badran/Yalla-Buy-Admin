package com.example.yallabuyadmin.coupons.model.test

import com.example.yallabuyadmin.coupons.model.CouponsRemoteDataSource
import com.example.yallabuyadmin.coupons.model.ICouponsRemoteDataSource
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.fake.FakeApiService
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CouponsRemoteDataSourceTest {

    private lateinit var fakeApiService: FakeApiService
    private lateinit var couponsRemoteDataSource: ICouponsRemoteDataSource

    @Before
    fun setup() {
        fakeApiService = FakeApiService()
        couponsRemoteDataSource = CouponsRemoteDataSource(fakeApiService)
    }

    @Test
    fun `test getPriceRules`() = runTest {
        val priceRule1 = PriceRule(title = "Rule 1", target_type = "line_item", value_type = "fixed_amount", value = 10.0, starts_at = "2023-01-01", ends_at = "2024-01-01", target_selection = "all", allocation_method = "across", customer_selection = "all", usage_limit = 1)
        val priceRule2 = PriceRule(title = "Rule 2", target_type = "shipping_line", value_type = "percentage", value = 15.0, starts_at = "2023-01-01", ends_at = "2024-01-01", target_selection = "all", allocation_method = "across", customer_selection = "all", usage_limit = 5)

        fakeApiService.createPriceRule(priceRuleRequest(priceRule1))
        fakeApiService.createPriceRule(priceRuleRequest(priceRule2))

        val response = couponsRemoteDataSource.getPriceRules()

        assertEquals(2, response.price_rules.size)
        assertEquals("Rule 1", response.price_rules[0].title)
        assertEquals("Rule 2", response.price_rules[1].title)
    }

    @Test
    fun `test createPriceRule`() = runTest {
        // Create a new price rule
        val newPriceRule = PriceRule(title = "New Rule", target_type = "line_item", value_type = "fixed_amount", value = 20.0, starts_at = "2023-01-01", ends_at = "2024-01-01", target_selection = "all", allocation_method = "across", customer_selection = "all", usage_limit = 2)
        couponsRemoteDataSource.createPriceRule(priceRuleRequest(newPriceRule))

        // Verify it was added
        val response = couponsRemoteDataSource.getPriceRules()
        assertEquals(1, response.price_rules.size)
        assertEquals("New Rule", response.price_rules[0].title)
    }
}
