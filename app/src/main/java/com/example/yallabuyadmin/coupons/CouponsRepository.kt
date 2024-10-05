package com.example.yallabuyadmin.coupons

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CouponsRepository(private val remoteDataSource: CouponsRemoteDataSource) {

    fun getPriceRules(): Flow<List<PriceRule>> = flow {
        try {
            val response = remoteDataSource.getPriceRules()
            emit(response.price_rules)
        } catch (e: Exception) {
            emit(emptyList()) // Emit an empty list in case of an error
        }
    }
    suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse {
        return remoteDataSource.createPriceRule(priceRule)
    }

    suspend fun deletePriceRule(priceRuleId: Long): Result<Unit> {
        return remoteDataSource.deletePriceRule(priceRuleId)
    }

    suspend fun getDiscountCodes(priceRuleId: Long) = remoteDataSource.getDiscountCodes(priceRuleId)

    suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCode) {
        remoteDataSource.createDiscountCode(priceRuleId, discountCode)
    }

    suspend fun updateDiscountCode(id: Long, discountCode: DiscountCode) {
        remoteDataSource.updateDiscountCode(id, discountCode)
    }

    suspend fun deleteDiscountCode(id: Long) {
        remoteDataSource.deleteDiscountCode(id)
    }
}
