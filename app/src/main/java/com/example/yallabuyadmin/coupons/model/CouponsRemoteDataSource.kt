package com.example.yallabuyadmin.coupons.model

import android.util.Log
import com.example.yallabuyadmin.network.ApiService
import retrofit2.HttpException
import retrofit2.Response

interface ICouponsRemoteDataSource {
    suspend fun getPriceRules(): priceRuleResponse

    suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse

    suspend fun deletePriceRule(priceRuleId: Long): Result<Unit>

    suspend fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest): Response<PriceRule>

    suspend fun getDiscountCodes(priceRuleId: Long): DiscountCodeResponse

    suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest)

    suspend fun updateDiscountCode(priceRuleId: Long, id: Long, discountCode: DiscountCodeRequest)

    suspend fun deleteDiscountCode(priceRuleId: Long, id: Long)
}

class CouponsRemoteDataSource(private val apiService: ApiService) : ICouponsRemoteDataSource {

    override suspend fun getPriceRules(): priceRuleResponse {
        return apiService.getPriceRules()
    }

    override suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse {
        return apiService.createPriceRule(priceRule)
    }
    override suspend fun deletePriceRule(priceRuleId: Long): Result<Unit> {
        return try {
            val response = apiService.deletePriceRule(priceRuleId)
            Log.d("API Response", response.toString())
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("Error", "Failed to delete price rule: ${response.code()}, Error Body: $errorBody")
                Result.failure(Throwable("Failed to delete price rule: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("Exception", "An error occurred: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest): Response<PriceRule> {
        return apiService.updatePriceRule(priceRuleId, priceRule)
//        return if (response.isSuccessful) {
//            Result.success(response.body()?.price_rule ?: error("No price rule returned"))
//        } else {
//            Result.failure(Exception("Failed to update price rule: ${response.message()}"))
//        }
    }
    override suspend fun getDiscountCodes(priceRuleId: Long): DiscountCodeResponse {
        Log.d("Discount Codes",apiService.getDiscountCodes(priceRuleId).toString())
        return apiService.getDiscountCodes(priceRuleId)
    }

    override suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest) {
        Log.d("Discount Code",discountCode.toString())
        apiService.createDiscountCode(priceRuleId, discountCode)
    }

    override suspend fun updateDiscountCode(priceRuleId: Long, id: Long, discountCode: DiscountCodeRequest) {
        try {
            Log.d("item",discountCode.toString())
            apiService.updateDiscountCode(priceRuleId,id, discountCode)
            Log.d("UpdateDiscountCode", "Discount code updated successfully.")
        } catch (e: HttpException) {
            Log.e("UpdateError", "HTTP Error: ${e.code()} - ${e.message()}")
        } catch (e: Exception) {
            Log.e("UpdateError", "Error updating discount code: ${e.message}")
        }
    }



    override suspend fun deleteDiscountCode(priceRuleId: Long, id: Long) {
        apiService.deleteDiscountCode(priceRuleId,id)
    }
}
