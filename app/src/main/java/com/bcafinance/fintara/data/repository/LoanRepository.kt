package com.bcafinance.fintara.data.repository

import android.util.Log
import com.bcafinance.fintara.config.network.api.LoanApiService
import com.bcafinance.fintara.data.model.dto.loan.LoanRequest
import com.bcafinance.fintara.data.model.dto.loan.LoanRequestResponse
import com.bcafinance.fintara.data.model.ApiResponse

class LoanRepository(private val apiService: LoanApiService) {
    suspend fun createLoanRequest(loanRequest: LoanRequest): ApiResponse<LoanRequestResponse> {
        Log.d("LoanRepository", "Request Data: Amount = ${loanRequest.amount}, Tenor = ${loanRequest.tenor}, Latitude = ${loanRequest.latitude}, Longitude = ${loanRequest.longitude}")
        return apiService.createLoanRequest(loanRequest)
    }

    suspend fun getInProgressLoan(): LoanRequestResponse? {
        val response = apiService.getInProgressLoanRequests()
        return response.data?.firstOrNull() // ambil satu jika ada
    }
}
