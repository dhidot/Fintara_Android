package com.bcafinance.fintara.data.model.dto.customer

import java.math.BigDecimal

data class DebtInfoResponse(
    val remainingPlafond: BigDecimal,
    val activeLoansCount: Int,
    val totalRepayment: BigDecimal
)