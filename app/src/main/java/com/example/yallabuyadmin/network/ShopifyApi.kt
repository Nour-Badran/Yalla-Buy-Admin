package com.example.yallabuyadmin.network

import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.DiscountCodeRequest
import com.example.yallabuyadmin.coupons.model.DiscountCodeResponse
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import com.example.yallabuyadmin.coupons.model.priceRuleResponse
import com.example.yallabuyadmin.products.model.ProductResponse
import com.example.yallabuyadmin.products.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.*

data class CountResponse(val count: Int)

interface ApiService {
    @GET("products.json")
    suspend fun getAllProducts(): ProductsResponse

    @POST("products.json")
    suspend fun createProduct(@Body product: ProductResponse): ProductResponse

    @PUT("products/{id}.json")
    suspend fun updateProduct(@Path("id") productId: Long, @Body product: ProductResponse): ProductResponse

    @DELETE("products/{id}.json")
    suspend fun deleteProduct(@Path("id") productId: Long): Response<Void>

    //Menu
    @GET("locations/count.json")
    suspend fun getInventoryCount(): CountResponse

    @GET("products/count.json")
    suspend fun getProductsCount(): CountResponse

    @GET("discount_codes/count.json")
    suspend fun getCouponsCount(): CountResponse

    //Price Rules

    @GET("price_rules.json")
    suspend fun getPriceRules(): priceRuleResponse

    @POST("price_rules.json")
    suspend fun createPriceRule(@Body priceRule: priceRuleRequest): priceRuleResponse

    @DELETE("price_rules/{priceRuleId}.json")
    suspend fun deletePriceRule(
        @Path("priceRuleId") priceRuleId: Long
    ): Response<Unit>

    @PUT("price_rules/{priceRuleId}.json")
    suspend fun updatePriceRule(
        @Path("priceRuleId") priceRuleId: Long,
        @Body priceRuleRequest: priceRuleRequest
    ): Response<PriceRule>

    // Discount Codes

    @GET("price_rules/{price_rule_id}/discount_codes.json")
    suspend fun getDiscountCodes(
        @Path("price_rule_id") priceRuleId: Long
    ): DiscountCodeResponse

    @POST("price_rules/{price_rule_id}/discount_codes.json")
    suspend fun createDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Body discountCode: DiscountCodeRequest
    )

    @PUT("discount_codes/{id}.json")
    suspend fun updateDiscountCode(
        @Path("id") id: Long,
        @Body discountCode: DiscountCode
    )

    @DELETE("discount_codes/{id}.json")
    suspend fun deleteDiscountCode(
        @Path("id") id: Long
    )
}

