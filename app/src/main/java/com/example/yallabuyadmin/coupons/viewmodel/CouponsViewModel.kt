package com.example.yallabuyadmin.coupons.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuyadmin.coupons.model.CouponsRepository
import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.DiscountCodeRequest
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CouponsViewModel(private val couponsRepository: CouponsRepository) : ViewModel() {

    private val _discountCodes = MutableStateFlow<List<DiscountCode>>(emptyList())
    val discountCodes: StateFlow<List<DiscountCode>> get() = _discountCodes

    private var priceRuleId: Long? = null

    private val _priceRules = MutableStateFlow<List<PriceRule>>(emptyList())
    val priceRules: StateFlow<List<PriceRule>> get() = _priceRules

    private val _deleteResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteResult: StateFlow<Result<Unit>?> = _deleteResult

    fun fetchPriceRules() {
        viewModelScope.launch {
            couponsRepository.getPriceRules().collect { rules ->
                _priceRules.value = rules
            }
        }
    }

    fun createNewPriceRule(priceRule: priceRuleRequest) {
        viewModelScope.launch {
            try {
                Log.d("price:",priceRule.toString())
                couponsRepository.createPriceRule(priceRule)
                fetchPriceRules()
                //val ay7aga = couponsRepository.getPriceRules()
                //priceRuleId = createdPriceRule.price_rule.get(0).id // Store the created price rule ID
                //fetchDiscountCodes(priceRuleId!!) // Fetch discount codes after creating the price rule
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("Error", "Failed to create price rule: ${e.message()}, Error Body: $errorBody")
            } catch (e: Exception) {
                Log.e("Error", "An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun updatePriceRule(priceRuleId: Long, priceRule: priceRuleRequest) {
        viewModelScope.launch {
            try{
                val result = couponsRepository.updatePriceRule(priceRuleId, priceRule)
                Log.d("Result",result.toString())
                //_deleteResult.value = result
                fetchPriceRules()
            }  catch (e: Exception) {
                // Handle errors accordingly
                e.printStackTrace()
            }
        }
    }

    fun deletePriceRule(priceRuleId: Long) {
        viewModelScope.launch {
            try{
                val result = couponsRepository.deletePriceRule(priceRuleId)
                Log.d("Result",result.toString())
                _deleteResult.value = result
                fetchPriceRules()
            }  catch (e: Exception) {
                // Handle errors accordingly
                e.printStackTrace()
            }
        }
    }
    fun fetchDiscountCodes(priceRuleId: Long) {
        viewModelScope.launch {
            couponsRepository.getDiscountCodes(priceRuleId).collect { discountCodes ->
                _discountCodes.value = discountCodes
            }
        }
    }

    fun createDiscountCode(priceRuleId: Long, discountCode: DiscountCodeRequest) {
        viewModelScope.launch {
            try {
                couponsRepository.createDiscountCode(priceRuleId, discountCode)
                fetchDiscountCodes(priceRuleId) // Refresh the list after creation
            }catch (e: HttpException) {
                if (e.code() == 404) {
                    // Show an error message to the user about the not found error
                    Log.e("Error", "Discount code creation failed: Resource not found")
                } else {
                    // Handle other HTTP errors
                    Log.e("Error", "HTTP error occurred: ${e.message()}")
                }
            } catch (e: Exception) {
                // Handle non-HTTP exceptions
                Log.e("Error", "An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun updateDiscountCode(priceRuleId: Long,id: Long, discountCode: DiscountCodeRequest) {
        viewModelScope.launch {
            try {
                couponsRepository.updateDiscountCode(priceRuleId,id, discountCode)
                fetchDiscountCodes(priceRuleId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteDiscountCode(priceRuleId: Long,id: Long) {
        viewModelScope.launch {
            try {
                couponsRepository.deleteDiscountCode(priceRuleId,id)
                fetchDiscountCodes(priceRuleId) // Refresh discount codes
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}