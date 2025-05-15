package com.bcafinance.fintara.data.model.dto.loan

import java.math.BigDecimal
import java.util.UUID

data class LoanRequestResponse(
    val customerId: UUID,
    val customerName: String,
    val loanRequestId: UUID,
    val amount: BigDecimal,
    val tenor: Int,
    val branchId: UUID,
    val branchName: String,
    val marketingId: UUID,
    val marketingName: String,
    val marketingEmail: String,
    val marketingNip: String,
    val status: String
)