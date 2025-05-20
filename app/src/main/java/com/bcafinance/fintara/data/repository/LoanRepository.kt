package com.bcafinance.fintara.data.repository

import android.util.Log
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.config.network.api.LoanApiService
import com.bcafinance.fintara.data.model.dto.loan.LoanRequest
import com.bcafinance.fintara.data.model.dto.loan.LoanRequestResponse
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.loan.LoanHistoryResponse
import com.bcafinance.fintara.data.model.dto.loan.LoanPreviewResponse

class LoanRepository(private val apiService: LoanApiService) {
    suspend fun createLoanRequest(loanRequest: LoanRequest): ApiResponse<LoanRequestResponse> {
        Log.d("LoanRepository", "Request Data: Amount = ${loanRequest.amount}, Tenor = ${loanRequest.tenor}, Latitude = ${loanRequest.latitude}, Longitude = ${loanRequest.longitude}")
        return apiService.createLoanRequest(loanRequest)
    }

    suspend fun getInProgressLoan(): LoanRequestResponse? {
        val response = apiService.getInProgressLoanRequests()
        return response.data?.firstOrNull() // ambil satu jika ada
    }

    suspend fun getLoanPreview(request: LoanRequest): LoanPreviewResponse {
        val response = apiService.previewLoan(request)
        if (response.status != 200) {
            throw Exception(response.getFormattedMessages())
        }
        return response.data!!
    }

    suspend fun getLoanHistory(status: String): List<LoanHistoryResponse>? {
        val response = apiService.getLoanHistory(status)
        if (response.status == 200) {
            return response.data!!
        }
        return null
    }
}
