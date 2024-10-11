package com.example.yallabuyadmin.coupons.model.fake

import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.DiscountCodeRequest
import com.example.yallabuyadmin.coupons.model.DiscountCodeResponse
import com.example.yallabuyadmin.coupons.model.ICouponsRemoteDataSource
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import com.example.yallabuyadmin.coupons.model.priceRuleResponse
import retrofit2.Response

class FakeCouponsRemoteDataSource : ICouponsRemoteDataSource {
    private val fakePriceRules = listOf(
        PriceRule(id = 1, title = "Rule 1", target_type = "product", value_type = "percentage", value = 10.0, starts_at = "2024-01-01", ends_at = "2024-12-31", target_selection = "all", allocation_method = "across", customer_selection = "all", usage_limit = 100),
        PriceRule(id = 2, title = "Rule 2", target_type = "product", value_type = "fixed_amount", value = 20.0, starts_at = "2024-01-01", ends_at = "2024-12-31", target_selection = "specific", allocation_method = "each", customer_selection = "specific", usage_limit = 50)
    )

    private val fakeDiscountCodes = listOf(
        DiscountCode(id = 1, code = "DISCOUNT1", usage_count = 10, created_at = "2024-01-01"),
        DiscountCode(id = 2, code = "DISCOUNT2", usage_count = 5, created_at = "2024-01-02")
    )

    override suspend fun getPriceRules(): priceRuleResponse {
        return priceRuleResponse(price_rules = fakePriceRules)
    }

    override suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse {
        val newRule = PriceRule(id = 3, title = priceRule.price_rule.title, target_type = priceRule.price_rule.target_type, value_type = priceRule.price_rule.value_type, value = priceRule.price_rule.value, starts_at = priceRule.price_rule.starts_at, ends_at = priceRule.price_rule.ends_at, target_selection = priceRule.price_rule.target_selection, allocation_method = priceRule.price_rule.allocation_method, customer_selection = priceRule.price_rule.customer_selection, usage_limit = priceRule.price_rule.usage_limit)
        return priceRuleResponse(price_rules = fakePriceRules + newRule)
    }

    override suspend fun deletePriceRule(priceRuleId: Long): Result<Unit> {
        return Result.success(Unit) // Simulate successful deletion
    }

    override suspend fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest): Response<PriceRule> {
        val updatedRule = PriceRule(id = priceRuleId, title = priceRule.price_rule.title, target_type = priceRule.price_rule.target_type, value_type = priceRule.price_rule.value_type, value = priceRule.price_rule.value, starts_at = priceRule.price_rule.starts_at, ends_at = priceRule.price_rule.ends_at, target_selection = priceRule.price_rule.target_selection, allocation_method = priceRule.price_rule.allocation_method, customer_selection = priceRule.price_rule.customer_selection, usage_limit = priceRule.price_rule.usage_limit)
        return Response.success(updatedRule)
    }

    override suspend fun getDiscountCodes(priceRuleId: Long): DiscountCodeResponse {
        return DiscountCodeResponse(discount_codes = fakeDiscountCodes)
    }

    override suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest) {
        // Simulate creating a discount code
    }

    override suspend fun updateDiscountCode(priceRuleId: Long, id: Long, discountCode: DiscountCodeRequest) {
        // Simulate updating a discount code
    }

    override suspend fun deleteDiscountCode(priceRuleId: Long, id: Long) {
        // Simulate successful deletion
    }
}
