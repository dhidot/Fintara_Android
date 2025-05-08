package com.bcafinance.fintara.data.model

import java.math.BigDecimal

data class Plafond(
    val id: String,
    val name: String,
    val maxAmount: BigDecimal,
    val interestRate: Double,
    val minTenor: Int,
    val maxTenor: Int
)
