package com.example.yallabuyadmin.network

import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.DiscountCodeRequest
import com.example.yallabuyadmin.coupons.model.DiscountCodeResponse
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import com.example.yallabuyadmin.coupons.model.priceRuleResponse
import com.example.yallabuyadmin.products.model.ProductResponse
import com.example.yallabuyadmin.products.model.ProductsResponse
import com.example.yallabuyadmin.products.model.Variant
import com.example.yallabuyadmin.products.model.VariantRequest
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

    //variants
    @PUT("variants/{id}.json")
    suspend fun updateVariant(@Path("id") variantId: Long, @Body variantRequest: VariantRequest)

    @DELETE("products/{product_id}/variants/{variant_id}.json")
    suspend fun deleteVariant(
        @Path("product_id") productId: Long,
        @Path("variant_id") variantId: Long
    ): Response<Void>


    @POST("products/{product_id}/variants.json")
    suspend fun createVariant(@Path("product_id") productId: Long, @Body variant: VariantRequest)
    //Create Delete

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

    @PUT("price_rules/{price_rule_id}/discount_codes/{id}.json")
    suspend fun updateDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Path("id") id: Long,
        @Body discountCode: DiscountCodeRequest
    )

    @DELETE("price_rules/{price_rule_id}/discount_codes/{id}.json")
    suspend fun deleteDiscountCode(
        @Path("price_rule_id") priceRuleId: Long,
        @Path("id") id: Long
    )
}

