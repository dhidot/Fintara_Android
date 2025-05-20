package com.bcafinance.fintara.data.model.dto.loan

import java.math.BigDecimal

data class LoanPreviewResponse(
    val requestedAmount: BigDecimal,
    val disbursedAmount: BigDecimal,
    val tenor: Int,
    val interestRate: BigDecimal,
    val interestAmount: BigDecimal,
    val feesAmount: BigDecimal,
    val totalRepayment: BigDecimal,
    val estimatedInstallment: BigDecimal
)
