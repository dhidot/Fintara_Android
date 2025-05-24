package com.bcafinance.fintara.data.model.dto.loan

data class LoanHistoryResponse(
    val id: String,
    val amount: Double,
    val tenor: Int,
    val interestAmount: Double,
    val disbursedAmount: Double,
    val status: String,
    val createdAt: String
)
