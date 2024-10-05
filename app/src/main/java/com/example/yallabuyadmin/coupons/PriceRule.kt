package com.example.yallabuyadmin.coupons

data class PriceRule(
    val id: Long? = null,
    val title: String,
    val target_type: String,
    val value_type: String,
    val value: Double,
    val starts_at: String,
    val ends_at: String,
    val target_selection: String,
    val allocation_method: String,
    val customer_selection: String
)

data class priceRuleResponse(
    val price_rules: List<PriceRule>
)

data class priceRuleRequest(
    val price_rule: PriceRule
)

data class DiscountCode(
    val id: Long,
    val code: String,
    val usageCount: Int,
    val createdAt: String
)
