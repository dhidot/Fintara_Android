package com.bcafinance.fintara.data.repository

import com.bcafinance.fintara.config.network.api.RepaymentApiService
import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import javax.inject.Inject

class RepaymentRepository @Inject constructor(
    private val apiService: RepaymentApiService
) {
    suspend fun getRepayments(loanRequestId: String): List<RepaymentsScheduleResponse> {
        val response = apiService.getRepaymentSchedules(loanRequestId)
        return response.data!!
    }
}
