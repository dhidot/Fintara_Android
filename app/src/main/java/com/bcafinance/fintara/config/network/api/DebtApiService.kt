package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.customer.DebtInfoResponse
import retrofit2.http.GET

interface DebtApiService {
    @GET("api/v1/debt-info/me")
    suspend fun getDebtInfo(): ApiResponse<DebtInfoResponse>
}