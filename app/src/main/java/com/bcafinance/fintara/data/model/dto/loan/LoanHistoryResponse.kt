package com.bcafinance.fintara.data.model.dto.loan

data class LoanHistoryResponse(
    val id: String,
    val amount: Double,
    val plafondName: String,
    val status: String,
    val createdAt: String
)