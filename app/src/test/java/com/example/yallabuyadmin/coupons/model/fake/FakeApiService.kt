package com.example.yallabuyadmin.coupons.model.fake
import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.DiscountCodeRequest
import com.example.yallabuyadmin.coupons.model.DiscountCodeResponse
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import com.example.yallabuyadmin.coupons.model.priceRuleResponse
import com.example.yallabuyadmin.network.ApiService
import com.example.yallabuyadmin.network.CountResponse
import com.example.yallabuyadmin.products.model.*
import okhttp3.ResponseBody
import retrofit2.Response

class FakeApiService : ApiService {

    private val priceRules = mutableListOf<PriceRule>()
    private val discountCodesMap = mutableMapOf<Long, MutableList<DiscountCode>>() // Maps PriceRule ID to DiscountCodes
    private var inventoryCount: Int = 0
    private var productsCount: Int = 0
    private var couponsCount: Int = 0

    override suspend fun getAllProducts(): ProductsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun createProduct(product: ProductResponse): ProductResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateProduct(productId: Long, product: ProductResponse): ProductResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productId: Long): Response<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun updateVariant(variantId: Long, variantRequest: VariantRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVariant(productId: Long, variantId: Long): Response<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun getVariants(productId: Long): VariantResponse {
        TODO("Not yet implemented")
    }

    override suspend fun createVariant(productId: Long, variant: VariantRequest) {
        TODO("Not yet implemented")
    }

    fun setInventoryCount(count: Int) {
        inventoryCount = count
    }

    fun setProductsCount(count: Int) {
        productsCount = count
    }

    fun setCouponsCount(count: Int) {
        couponsCount = count
    }

    override suspend fun getInventoryCount(): CountResponse {
        return CountResponse(count = inventoryCount)
    }

    override suspend fun getProductsCount(): CountResponse {
        return CountResponse(count = productsCount)
    }

    override suspend fun getCouponsCount(): CountResponse {
        return CountResponse(count = couponsCount)
    }


    // Simulate fetching price rules
    override suspend fun getPriceRules(): priceRuleResponse {
        return priceRuleResponse(price_rules = priceRules)
    }

    // Simulate creating a price rule
    override suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse {
        val newPriceRule = priceRule.price_rule.copy(id = (priceRules.size + 1).toLong())
        priceRules.add(newPriceRule)
        return priceRuleResponse(price_rules = priceRules)
    }

    // Simulate updating a price rule
    override suspend fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest): Response<PriceRule> {
        val index = priceRules.indexOfFirst { it.id == priceRuleId }
        return if (index != -1) {
            priceRules[index] = priceRule.price_rule.copy(id = priceRuleId)
            Response.success(priceRules[index])
        } else {
            Response.error(404, ResponseBody.create(null, "PriceRule not found"))
        }
    }

    // Simulate deleting a price rule
    override suspend fun deletePriceRule(priceRuleId: Long): Response<Unit> {
        val index = priceRules.indexOfFirst { it.id == priceRuleId }
        return if (index != -1) {
            priceRules.removeAt(index)
            Response.success(Unit)
        } else {
            Response.error(404, ResponseBody.create(null, "PriceRule not found"))
        }
    }

    // Simulate fetching discount codes
    override suspend fun getDiscountCodes(priceRuleId: Long): DiscountCodeResponse {
        return DiscountCodeResponse(discount_codes = discountCodesMap[priceRuleId] ?: emptyList())
    }

    // Simulate creating a discount code
    override suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest) {
        val discountCodes = discountCodesMap.getOrPut(priceRuleId) { mutableListOf() }
        val newDiscountCode = discountCode.discount_code.copy(id = (discountCodes.size + 1).toLong())
        discountCodes.add(newDiscountCode)
    }

    // Simulate updating a discount code
    override suspend fun updateDiscountCode(priceRuleId: Long, id: Long, discountCode: DiscountCodeRequest) {
        val discountCodes = discountCodesMap[priceRuleId]
        val index = discountCodes?.indexOfFirst { it.id == id }
        if (index != null && index != -1) {
            discountCodes[index] = discountCode.discount_code.copy(id = id)
        }
    }

    // Simulate deleting a discount code
    override suspend fun deleteDiscountCode(priceRuleId: Long, id: Long) {
        val discountCodes = discountCodesMap[priceRuleId]
        discountCodes?.removeIf { it.id == id }
    }
}
