package com.bcafinance.fintara.data.model.dto.loan

import java.math.BigDecimal
import java.util.UUID

data class LoanInProgressResponse(
    val customerId: UUID,
    val customerName: String,
    val amount: BigDecimal,
    val tenor: Int,
    val interestAmount: BigDecimal,
    val interestRate: BigDecimal,
    val feesAmount: BigDecimal,
    val branchName: String,
    val marketingName: String,
    val marketingEmail: String,
    val status: String
)