package com.example.yallabuyadmin.coupons.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

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

    suspend fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest): Response<PriceRule> {
        return remoteDataSource.updatePriceRule(priceRuleId, priceRule)
    }

    suspend fun deletePriceRule(priceRuleId: Long): Result<Unit> {
        return remoteDataSource.deletePriceRule(priceRuleId)
    }

    fun getDiscountCodes(priceRuleId: Long): Flow<List<DiscountCode>> = flow {
        try {
            val response = remoteDataSource.getDiscountCodes(priceRuleId)
            emit(response.discount_codes)
        } catch (e: Exception) {
            emit(emptyList()) // Emit an empty list in case of an error
        }
    }

    //suspend fun getDiscountCodes(priceRuleId: Long) = remoteDataSource.getDiscountCodes(priceRuleId)

    suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest) {
        remoteDataSource.createDiscountCode(priceRuleId, discountCode)
    }

    suspend fun updateDiscountCode(priceRuleId: Long,id: Long, discountCode: DiscountCodeRequest) {
        remoteDataSource.updateDiscountCode(priceRuleId,id, discountCode)
    }

    suspend fun deleteDiscountCode(priceRuleId: Long,id: Long) {
        remoteDataSource.deleteDiscountCode(priceRuleId,id)
    }
}
