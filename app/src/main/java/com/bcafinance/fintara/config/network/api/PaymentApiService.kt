package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {
    @POST("api/v1/payments/generate-token")
    suspend fun generateSnapToken(
        @Body request: SnapTokenRequest
    ): ApiResponse<String>
}

data class SnapTokenRequest(
    val repaymentScheduleId: String,
    val amount: Long
)
