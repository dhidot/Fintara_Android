package com.bcafinance.fintara.data.repository

import com.bcafinance.fintara.config.network.api.PaymentApiService
import com.bcafinance.fintara.config.network.api.SnapTokenRequest
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val apiService: PaymentApiService
) {
    suspend fun generateSnapToken(repaymentScheduleId: String, amount: Long): String? {
        val response = apiService.generateSnapToken(SnapTokenRequest(repaymentScheduleId, amount))
        return response.data
    }
}