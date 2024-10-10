package com.example.yallabuyadmin.coupons.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

interface ICouponsRepository {
    fun getPriceRules(): Flow<List<PriceRule>>

    suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse

    suspend fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest): Response<PriceRule>

    suspend fun deletePriceRule(priceRuleId: Long): Result<Unit>
    fun getDiscountCodes(priceRuleId: Long): Flow<List<DiscountCode>>

    suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest)

    suspend fun updateDiscountCode(priceRuleId: Long, id: Long, discountCode: DiscountCodeRequest)

    suspend fun deleteDiscountCode(priceRuleId: Long, id: Long)
}

class CouponsRepository(private val remoteDataSource: ICouponsRemoteDataSource) :
    ICouponsRepository {

    override fun getPriceRules(): Flow<List<PriceRule>> = flow {
        try {
            val response = remoteDataSource.getPriceRules()
            emit(response.price_rules)
        } catch (e: Exception) {
            emit(emptyList()) // Emit an empty list in case of an error
        }
    }
    override suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse {
        return remoteDataSource.createPriceRule(priceRule)
    }

    override suspend fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest): Response<PriceRule> {
        return remoteDataSource.updatePriceRule(priceRuleId, priceRule)
    }

    override suspend fun deletePriceRule(priceRuleId: Long): Result<Unit> {
        return remoteDataSource.deletePriceRule(priceRuleId)
    }

    override fun getDiscountCodes(priceRuleId: Long): Flow<List<DiscountCode>> = flow {
        try {
            val response = remoteDataSource.getDiscountCodes(priceRuleId)
            emit(response.discount_codes)
        } catch (e: Exception) {
            emit(emptyList()) // Emit an empty list in case of an error
        }
    }

    //suspend fun getDiscountCodes(priceRuleId: Long) = remoteDataSource.getDiscountCodes(priceRuleId)

    override suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest) {
        remoteDataSource.createDiscountCode(priceRuleId, discountCode)
    }

    override suspend fun updateDiscountCode(priceRuleId: Long, id: Long, discountCode: DiscountCodeRequest) {
        remoteDataSource.updateDiscountCode(priceRuleId,id, discountCode)
    }

    override suspend fun deleteDiscountCode(priceRuleId: Long, id: Long) {
        remoteDataSource.deleteDiscountCode(priceRuleId,id)
    }
}
