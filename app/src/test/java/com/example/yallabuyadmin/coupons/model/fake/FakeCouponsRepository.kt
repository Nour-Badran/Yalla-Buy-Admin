package com.example.yallabuyadmin.coupons.model.fake

import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.DiscountCodeRequest
import com.example.yallabuyadmin.coupons.model.ICouponsRepository
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import com.example.yallabuyadmin.coupons.model.priceRuleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeCouponsRepository : ICouponsRepository {
    private val priceRules = mutableListOf<PriceRule>()
    private val discountCodes = mutableListOf<DiscountCode>()

    // Initialize some fake price rules for testing
    init {
        priceRules.add(PriceRule(1, "Discount 1", "type1", "value_type1", 10.0, "2024-01-01", "2024-12-31", "target_selection1", "allocation_method1", "customer_selection1", 100))
        priceRules.add(PriceRule(2, "Discount 2", "type2", "value_type2", 20.0, "2024-01-01", "2024-12-31", "target_selection2", "allocation_method2", "customer_selection2", 50))
    }

    override fun getPriceRules(): Flow<List<PriceRule>> = flow {
        emit(priceRules) // Emit the list of price rules
    }

    override suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse {
        // Simulate adding a new price rule
        val newRule = PriceRule(id = 3, title = priceRule.price_rule.title, target_type = priceRule.price_rule.target_type, value_type = priceRule.price_rule.value_type, value = priceRule.price_rule.value, starts_at = priceRule.price_rule.starts_at, ends_at = priceRule.price_rule.ends_at, target_selection = priceRule.price_rule.target_selection, allocation_method = priceRule.price_rule.allocation_method, customer_selection = priceRule.price_rule.customer_selection, usage_limit = priceRule.price_rule.usage_limit)
        priceRules.add(newRule)
        return priceRuleResponse(price_rules = priceRules)
    }

    override suspend fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest): Response<PriceRule> {
        // Simulate updating a price rule
        val index = priceRules.indexOfFirst { it.id == priceRuleId }
        return if (index != -1) {
            priceRules[index] = priceRule.price_rule.copy(id = priceRuleId)
            Response.success(priceRules[index])
        } else {
            Response.error(404, null)
        }
    }

    override suspend fun deletePriceRule(priceRuleId: Long): Result<Unit> {
        // Simulate deleting a price rule
        val index = priceRules.indexOfFirst { it.id == priceRuleId }
        return if (index != -1) {
            priceRules.removeAt(index)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Not found"))
        }
    }

    override fun getDiscountCodes(priceRuleId: Long): Flow<List<DiscountCode>> = flow {
        emit(discountCodes) // Emit the list of discount codes
    }

    override suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest) {
        // Simulate adding a new discount code
        val newCode = DiscountCode(id = 3, code = discountCode.discount_code.code, usage_count = 0, created_at = "2024-01-01")
        discountCodes.add(newCode)
    }

    override suspend fun updateDiscountCode(priceRuleId: Long, id: Long, discountCode: DiscountCodeRequest) {
        // Simulate updating a discount code
        val index = discountCodes.indexOfFirst { it.id == id }
        if (index != -1) {
            discountCodes[index] = discountCode.discount_code.copy(id = id)
        }
    }

    override suspend fun deleteDiscountCode(priceRuleId: Long, id: Long) {
        // Simulate deleting a discount code
        discountCodes.removeAll { it.id == id }
    }
}
