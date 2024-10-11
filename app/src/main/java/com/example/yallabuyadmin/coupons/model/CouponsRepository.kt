package com.example.yallabuyadmin.coupons.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

class CouponsRepository(private val remoteDataSource: ICouponsRemoteDataSource) : ICouponsRepository {

    override fun getPriceRules(): Flow<List<PriceRule>> = flow {
        val response = remoteDataSource.getPriceRules()
        emit(response.price_rules) // Emit the list of price rules
    }.catch { e ->
        // Handle the exception here
        emit(emptyList()) // Emit an empty list if an error occurs
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
            val response = remoteDataSource.getDiscountCodes(priceRuleId)
            emit(response.discount_codes)
        }.catch {
            e -> emit(emptyList())
    }

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
