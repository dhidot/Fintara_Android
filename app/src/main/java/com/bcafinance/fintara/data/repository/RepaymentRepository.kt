package com.bcafinance.fintara.data.repository

import com.bcafinance.fintara.config.network.api.RepaymentApiService
import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import javax.inject.Inject
import android.util.Log

class RepaymentRepository @Inject constructor(
    private val apiService: RepaymentApiService
) {
    suspend fun getRepayments(loanRequestId: String): List<RepaymentsScheduleResponse> {
        val response = apiService.getRepaymentSchedules(loanRequestId)
        val data = response.data
        Log.d("Repo", "Response data: $data")
        return data ?: emptyList()
    }
}
