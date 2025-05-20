package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.loan.LoanHistoryResponse
import com.bcafinance.fintara.data.model.dto.loan.LoanPreviewResponse
import retrofit2.http.Body
import retrofit2.http.POST
import com.bcafinance.fintara.data.model.dto.loan.LoanRequest
import com.bcafinance.fintara.data.model.dto.loan.LoanRequestResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LoanApiService {
    @POST("api/v1/loan-requests")
    suspend fun createLoanRequest(@Body request : LoanRequest): ApiResponse<LoanRequestResponse>

    @GET("api/v1/loan-requests/in-progress")
    suspend fun getInProgressLoanRequests(): ApiResponse<List<LoanRequestResponse>>

    @POST("api/v1/loan-requests/loan-preview")
    suspend fun previewLoan(@Body request: LoanRequest): ApiResponse<LoanPreviewResponse>

    @GET("api/v1/loan-requests/history")
    suspend fun getLoanHistory(
        @Query("status") status: String // "APPROVED" atau "REJECTED"
    ): ApiResponse<List<LoanHistoryResponse>>
}