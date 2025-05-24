package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface RepaymentApiService {
    @GET("/api/v1/repayments/{loanRequestId}")
    suspend fun getRepaymentSchedules(
        @Path("loanRequestId") loanRequestId: String
    ): ApiResponse<List<RepaymentsScheduleResponse>>
}
