package com.example.yallabuyadmin.coupons

import android.util.Log
import com.example.yallabuyadmin.network.ApiService

class CouponsRemoteDataSource(private val apiService: ApiService) {

    suspend fun getPriceRules(): priceRuleResponse {
        return apiService.getPriceRules()
    }

    suspend fun createPriceRule(priceRule: priceRuleRequest): priceRuleResponse {
        return apiService.createPriceRule(priceRule)
    }
    suspend fun deletePriceRule(priceRuleId: Long): Result<Unit> {
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



    suspend fun getDiscountCodes(priceRuleId: Long): List<DiscountCode> {
        return apiService.getDiscountCodes(priceRuleId)
    }

    suspend fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCode) {
        apiService.createDiscountCode(priceRuleId, discountCode)
    }

    suspend fun updateDiscountCode(id: Long, discountCode: DiscountCode) {
        apiService.updateDiscountCode(id, discountCode)
    }

    suspend fun deleteDiscountCode(id: Long) {
        apiService.deleteDiscountCode(id)
    }
}
