package com.bcafinance.fintara.data.model.dto.loan

import java.math.BigDecimal

data class LoanSimulationRequest(
    val amount: BigDecimal,
    val tenor: Int
)
